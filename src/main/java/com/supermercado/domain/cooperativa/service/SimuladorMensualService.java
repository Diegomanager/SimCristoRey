package com.supermercado.domain.cooperativa.service;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.dto.RegistroAtencion;
import com.supermercado.domain.cooperativa.config.ConfiguracionCooperativa;
import com.supermercado.domain.cooperativa.event.EventoSimulacion;
import com.supermercado.domain.cooperativa.event.TipoEvento;
import com.supermercado.domain.cooperativa.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class SimuladorMensualService {

    private final SimuladorCooperativaService     motor;
    private final List<ResumenDiario>             resumenes = new ArrayList<>();
    private final List<Consumer<EventoSimulacion>> listeners = new CopyOnWriteArrayList<>();

    private ConfiguracionCooperativa   config;
    private List<JornadaLaboral>       jornadas;
    private List<Caja>                 cajas;
    private List<ServicioFinanciero>   servicios;
    private ConfiguracionMultiServicio configMulti;

    private CalibracionMensual calibracion;

    private volatile boolean corriendo = false;
    private int diaEnCurso = 0;

    private boolean       preguntarRezagadosHabilitado = false;
    private BooleanSupplier preguntaRezagados = () -> true;

    public SimuladorMensualService(SimuladorCooperativaService motor) {
        this.motor = motor;
        motor.addListener(this::propagar);
    }

    public void configurar(ConfiguracionCooperativa cfg, List<JornadaLaboral> jornadas,
                           List<Caja> cajas, List<ServicioFinanciero> servicios,
                           ConfiguracionMultiServicio multi) {
        this.config=cfg; this.jornadas=jornadas; this.cajas=cajas;
        this.servicios=servicios; this.configMulti=multi;
    }

    public void setCalibracion(CalibracionMensual cal) { this.calibracion = cal; }

    public void setPreguntaRezagados(BooleanSupplier fn)       { preguntaRezagados=fn; }
    public void setPreguntarRezagadosHabilitado(boolean v)     { preguntarRezagadosHabilitado=v; }

    public void iniciar() {
        if (corriendo) return;
        corriendo=true; resumenes.clear();
        Thread h=new Thread(this::bucle,"Motor-Mensual");
        h.setDaemon(true); h.start();
    }

    public void pausar()  { motor.pausar(); }
    public void reanudar(){ motor.reanudar(); }

    public void detener() {
        corriendo=false; motor.detener();
        propagar(new EventoSimulacion(TipoEvento.SIMULACION_DETENIDA,"Detenida",""));
    }

    private void bucle() {
        int contadorLaborable = 0;
        int idxFechaCalibrada = 0;
        boolean hayReplay = calibracion != null
                && calibracion.isTieneFechas()
                && calibracion.getRegistros() != null
                && !calibracion.getRegistros().isEmpty();

        for (JornadaLaboral jornada : jornadas) {
            if (!corriendo) break;
            diaEnCurso = jornada.getDia();

            if (!jornada.isLaborable()) {
                resumenes.add(new ResumenDiario(diaEnCurso, false));
                continue;
            }

            contadorLaborable++;
            motor.setDiaSimulado(contadorLaborable);

            cajas.forEach(Caja::reiniciar);
            motor.configurar(config.getMsPorMinuto(), config.getMaxSociosDia(),
                    config.getIntervaloMinutos(), servicios, cajas, configMulti, config);

            LocalDate fechaReal = null;
            List<Socio> sociosPredefinidos = null;
            if (hayReplay && idxFechaCalibrada < calibracion.getDiasLaborables().size()) {
                fechaReal = calibracion.getDiasLaborables().get(idxFechaCalibrada);
                idxFechaCalibrada++;
                sociosPredefinidos = construirSociosDelDia(fechaReal);
        System.out.println(">>> [Replay] Socios predefinidos para " + fechaReal + ": " + (sociosPredefinidos != null ? sociosPredefinidos.size() : 0));
            }
            motor.setSociosPredefinidos(sociosPredefinidos);

            propagar(new EventoSimulacion(TipoEvento.DIA_INICIADO,
                    "=== Dia " + contadorLaborable + " iniciado"
                    + (fechaReal != null ? " (" + fechaReal + ")" : "") + " ===",
                    "Dia " + contadorLaborable));

            motor.iniciar(jornada, diaEnCurso);
            esperarCondicion(() -> motor.isFasePrincipalFinalizada());
            if (!corriendo) break;

            int enSala = motor.getSalaEspera()!=null ? motor.getSalaEspera().getTotalEsperando():0;
            int cajasOc= motor.getCajas()!=null
                    ? (int)motor.getCajas().stream().filter(c->c.getEstado()==EstadoCaja.OCUPADA).count():0;
            boolean hayRez = enSala>0 || cajasOc>0;

            if (hayRez) {
                boolean iniciar = preguntarRezagadosHabilitado
                        ? preguntaRezagados.getAsBoolean() : true;
                if (iniciar) {
                    motor.iniciarFaseRezagados();
                    esperarCondicion(() -> !motor.isCorriendo());
                } else {
                    propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                            "Rezagados omitidos: " + enSala + " socios",
                            motor.horaSimulada()));
                }
            } else {
                propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                        "Dia " + contadorLaborable + " finalizado (sin rezagados)",
                        motor.horaSimulada()));
            }
            if (!corriendo) break;

            ResumenDiario resumen = motor.construirResumenDia();
            resumen.setFecha(fechaReal);
            resumenes.add(resumen);
            motor.getEstadisticas().registrarResumenDiario(resumen);

            propagar(new EventoSimulacion(TipoEvento.DIA_FINALIZADO,
                    "Dia " + contadorLaborable
                    + (fechaReal != null ? " (" + fechaReal + ")" : "")
                    + " completo | Gen:" + resumen.getGenerados()
                    + " | Atend:" + resumen.getTotalAtendidos()
                    + " | Bs " + String.format("%.0f",resumen.getMontoTotal()),
                    "Dia " + contadorLaborable));

            try { Thread.sleep(500); } catch (InterruptedException e) { break; }
        }

        if (corriendo) {
            corriendo=false;
            long labs  = resumenes.stream().filter(ResumenDiario::isLaborable).count();
            int  atend = resumenes.stream().mapToInt(ResumenDiario::getTotalAtendidos).sum();
            propagar(new EventoSimulacion(TipoEvento.SIMULACION_MENSUAL_FINALIZADA,
                    "Finalizado | " + labs + " dias laborables | Total: " + atend + " atendidos",
                    ""));
        }
    }

    private List<Socio> construirSociosDelDia(LocalDate fecha) {
        List<Socio> lista = new ArrayList<>();
        int n = 1;
        for (RegistroAtencion r : calibracion.getRegistros()) {
            if (!fecha.equals(r.getFecha())) continue;

            Socio s = new Socio();
            s.setId("REPLAY-" + fecha + "-" + n);
            s.setFicha(r.getCodigoServicio() + String.format("%03d", n));
            s.setEsPreferente(r.isEsPreferencial());
            s.setPrioridad(r.isEsPreferencial() ? 1 : 3);
            s.setDuracionEstimada(Math.max(1, r.getDuracionMinutos()));
            s.setTiempoLlegada(r.getMinutoLlegada());
            s.setMonto(r.getMonto());

            boolean soloPlataforma = "P".equals(r.getCodigoServicio()) || "PP".equals(r.getCodigoServicio());
            String destino = soloPlataforma ? "PLATAFORMA" : "GENERAL";
            if (r.getMonto() >= 100000 && !soloPlataforma) destino = "MM";
            s.setTipoCajaDestino(destino);

            ServicioFinanciero svcRef = buscarServicioPorCodigo(r.getCodigoServicio());
            s.setServicio(svcRef);

            lista.add(s);
            n++;
        }
        return lista;
    }

    private ServicioFinanciero buscarServicioPorCodigo(String codigo) {
        if (servicios == null) return null;
        for (ServicioFinanciero s : servicios) {
            if (codigo.equals(s.getTipoCajaRequerido())) return s;
        }
        return null;
    }

    private void esperarCondicion(java.util.function.BooleanSupplier cond) {
        int t=0;
        while (corriendo && !cond.getAsBoolean()) {
            try{Thread.sleep(50);}catch(InterruptedException e){return;}
            if(++t>60000) break;
        }
    }

    private void propagar(EventoSimulacion ev) {
        for (var l:listeners){try{l.accept(ev);}catch(Exception ignored){}}
    }

    public void addListener(Consumer<EventoSimulacion> l)    { listeners.add(l); }
    public void removeListener(Consumer<EventoSimulacion> l) { listeners.remove(l); }
    public boolean isCorriendo()      { return corriendo; }
    public int     getDiaEnCurso()    { return diaEnCurso; }
    public List<ResumenDiario> getResumenes() { return Collections.unmodifiableList(resumenes); }
    public SimuladorCooperativaService getMotor() { return motor; }
}