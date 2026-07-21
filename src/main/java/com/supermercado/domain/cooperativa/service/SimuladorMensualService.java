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
    private int contadorLaborable = 0;

    // NUEVO: informacion real del replay, para que el Frame no dependa de
    // jornadasActuales (que es la lista del calendario MANUAL y puede
    // desalinearse si algun dia del historial no tiene socios).
    private volatile LocalDate fechaEnCurso = null;
    private volatile int totalDiasReplay = 0;
    private volatile boolean modoReplayActivo = false;

    private boolean preguntarRezagadosHabilitado = false;
    private BooleanSupplier preguntaRezagados = () -> true;

    public SimuladorMensualService(SimuladorCooperativaService motor) {
        this.motor = motor;
        motor.addListener(this::propagar);
    }

    public void configurar(ConfiguracionCooperativa cfg, List<JornadaLaboral> jornadas,
                           List<Caja> cajas, List<ServicioFinanciero> servicios,
                           ConfiguracionMultiServicio multi) {
        this.config = cfg;
        this.jornadas = jornadas;
        this.cajas = cajas;
        this.servicios = servicios;
        this.configMulti = multi;
    }

    public void setCalibracion(CalibracionMensual cal) {
        this.calibracion = cal;
        // Actualizar flag de modo replay para que el Frame pueda consultarlo sin iniciar
        this.modoReplayActivo = cal != null
                && cal.isTieneFechas()
                && cal.getRegistros() != null
                && !cal.getRegistros().isEmpty();
        System.out.println(">>> [Mensual] setCalibracion - modoReplayActivo=" + modoReplayActivo);
    }

    public void setPreguntaRezagados(BooleanSupplier fn) { preguntaRezagados = fn; }
    public void setPreguntarRezagadosHabilitado(boolean v) { preguntarRezagadosHabilitado = v; }

    // NUEVO: getters para que el Frame refleje fecha/progreso reales del replay
    public LocalDate getFechaEnCurso()   { return fechaEnCurso; }
    public int        getTotalDiasReplay() { return totalDiasReplay; }
    public boolean     isModoReplayActivo() { return modoReplayActivo; }

    public void iniciar() {
        if (corriendo) return;
        corriendo = true;
        resumenes.clear();
        fechaEnCurso = null;
        Thread h = new Thread(this::bucle, "Motor-Mensual");
        h.setDaemon(true);
        h.start();
    }

    public void pausar() { motor.pausar(); }
    public void reanudar() { motor.reanudar(); }
    public void detener() {
        corriendo = false;
        motor.detener();
        propagar(new EventoSimulacion(TipoEvento.SIMULACION_DETENIDA, "Detenida", ""));
    }

    private void bucle() {
        contadorLaborable = 0;
        fechaEnCurso = null;

        boolean hayReplay = calibracion != null
                && calibracion.isTieneFechas()
                && calibracion.getRegistros() != null
                && !calibracion.getRegistros().isEmpty();

        modoReplayActivo = hayReplay;

        if (hayReplay && calibracion.getDiasLaborables() != null) {
            totalDiasReplay = calibracion.getDiasLaborables().size();
            System.out.println(">>> [Replay] Iniciando modo replay con " + totalDiasReplay + " dias.");

            JornadaLaboral plantilla = jornadas.stream()
                    .filter(JornadaLaboral::isLaborable)
                    .findFirst()
                    .orElse(null);

            if (plantilla == null) {
                System.err.println(">>> [Replay] ERROR: No hay jornadas laborables en el calendario (horario base).");
                corriendo = false;
                return;
            }

            int idxDia = 0;
            for (LocalDate fechaReal : calibracion.getDiasLaborables()) {
                if (!corriendo) break;

                List<Socio> sociosDelDia = construirSociosDelDia(fechaReal);
                if (sociosDelDia == null || sociosDelDia.isEmpty()) {
                    System.out.println(">>> [Replay] Sin socios para " + fechaReal + ". Saltando.");
                    continue;
                }

                System.out.println(">>> [Replay] Procesando " + fechaReal + " con " + sociosDelDia.size() + " socios.");

                JornadaLaboral jornadaReal = new JornadaLaboral();
                jornadaReal.setDia(++idxDia);
                jornadaReal.setFechaReal(fechaReal);
                jornadaReal.setLaborable(true);
                for (BloqueHorario bh : plantilla.getBloques()) {
                    jornadaReal.agregarBloque(new BloqueHorario(bh.getInicio(), bh.getFin()));
                }

                ejecutarDiaConSocios(jornadaReal, sociosDelDia, fechaReal);
            }

            System.out.println(">>> [Replay] Todos los dias del historial procesados.");
            corriendo = false;
            propagar(new EventoSimulacion(TipoEvento.SIMULACION_MENSUAL_FINALIZADA,
                    "Replay completado - " + resumenes.size() + " dias procesados", ""));
            return;
        }

        // ==================== MODO MANUAL (sin cambios) ====================
        totalDiasReplay = 0;
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
            motor.setSociosPredefinidos(null);

            propagar(new EventoSimulacion(TipoEvento.DIA_INICIADO,
                    "=== Dia " + contadorLaborable + " iniciado ===",
                    "Dia " + contadorLaborable));

            motor.iniciar(jornada, diaEnCurso);
            esperarCondicion(() -> motor.isFasePrincipalFinalizada());
            if (!corriendo) break;

            int enSala = motor.getSalaEspera() != null ? motor.getSalaEspera().getTotalEsperando() : 0;
            int cajasOc = motor.getCajas() != null
                    ? (int) motor.getCajas().stream().filter(c -> c.getEstado() == EstadoCaja.OCUPADA).count()
                    : 0;
            boolean hayRez = enSala > 0 || cajasOc > 0;

            if (hayRez) {
                boolean iniciar = preguntarRezagadosHabilitado ? preguntaRezagados.getAsBoolean() : true;
                if (iniciar) {
                    motor.iniciarFaseRezagados();
                    esperarCondicion(() -> !motor.isCorriendo());
                } else {
                    propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                            "Rezagados omitidos: " + enSala + " socios", motor.horaSimulada()));
                }
            } else {
                propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                        "Dia " + contadorLaborable + " finalizado (sin rezagados)", motor.horaSimulada()));
            }
            if (!corriendo) break;

            ResumenDiario resumen = motor.construirResumenDia();
            resumenes.add(resumen);
            motor.getEstadisticas().registrarResumenDiario(resumen);

            propagar(new EventoSimulacion(TipoEvento.DIA_FINALIZADO,
                    "Dia " + contadorLaborable + " completo | Gen:" + resumen.getGenerados()
                            + " | Atend:" + resumen.getTotalAtendidos()
                            + " | Bs " + String.format("%.0f", resumen.getMontoTotal()),
                    "Dia " + contadorLaborable));

            try { Thread.sleep(500); } catch (InterruptedException e) { break; }
        }

        if (corriendo) {
            corriendo = false;
            long labs = resumenes.stream().filter(ResumenDiario::isLaborable).count();
            int atend = resumenes.stream().mapToInt(ResumenDiario::getTotalAtendidos).sum();
            propagar(new EventoSimulacion(TipoEvento.SIMULACION_MENSUAL_FINALIZADA,
                    "Finalizado | " + labs + " dias laborables | Total: " + atend + " atendidos", ""));
        }
    }

    private void ejecutarDiaConSocios(JornadaLaboral jornada, List<Socio> socios, LocalDate fechaReal) {
        diaEnCurso = jornada.getDia();
        contadorLaborable++;
        fechaEnCurso = fechaReal; // NUEVO: el Frame lee esto directamente
        motor.setDiaSimulado(contadorLaborable);
        cajas.forEach(Caja::reiniciar);
        motor.configurar(config.getMsPorMinuto(), config.getMaxSociosDia(),
                config.getIntervaloMinutos(), servicios, cajas, configMulti, config);
        motor.setSociosPredefinidos(socios);

        propagar(new EventoSimulacion(TipoEvento.DIA_INICIADO,
                "=== Dia " + contadorLaborable + " (" + fechaReal + ") iniciado ===",
                "Dia " + contadorLaborable));

        motor.iniciar(jornada, diaEnCurso);
        esperarCondicion(() -> motor.isFasePrincipalFinalizada());
        if (!corriendo) return;

        int enSala = motor.getSalaEspera() != null ? motor.getSalaEspera().getTotalEsperando() : 0;
        boolean hayRez = enSala > 0 || motor.getCajas().stream().anyMatch(c -> c.getEstado() == EstadoCaja.OCUPADA);

        if (hayRez) {
            boolean iniciar = preguntarRezagadosHabilitado ? preguntaRezagados.getAsBoolean() : true;
            if (iniciar) {
                motor.iniciarFaseRezagados();
                esperarCondicion(() -> !motor.isCorriendo());
            } else {
                propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                        "Rezagados omitidos: " + enSala + " socios", motor.horaSimulada()));
            }
        } else {
            propagar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                    "Dia " + contadorLaborable + " finalizado (sin rezagados)", motor.horaSimulada()));
        }
        if (!corriendo) return;

        ResumenDiario resumen = motor.construirResumenDia();
        resumen.setFecha(fechaReal);
        resumenes.add(resumen);
        motor.getEstadisticas().registrarResumenDiario(resumen);

        propagar(new EventoSimulacion(TipoEvento.DIA_FINALIZADO,
                "Dia " + contadorLaborable + " (" + fechaReal + ") completo | Gen:" +
                        resumen.getGenerados() + " | Atend:" + resumen.getTotalAtendidos(),
                "Dia " + contadorLaborable));

        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
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
        int t = 0;
        while (corriendo && !cond.getAsBoolean()) {
            try { Thread.sleep(50); } catch (InterruptedException e) { return; }
            if (++t > 60000) break;
        }
    }

    private void propagar(EventoSimulacion ev) {
        for (var l : listeners) {
            try { l.accept(ev); } catch (Exception ignored) {}
        }
    }

    public void addListener(Consumer<EventoSimulacion> l) { listeners.add(l); }
    public void removeListener(Consumer<EventoSimulacion> l) { listeners.remove(l); }
    public boolean isCorriendo() { return corriendo; }
    public int getDiaEnCurso() { return diaEnCurso; }
    public List<ResumenDiario> getResumenes() { return Collections.unmodifiableList(resumenes); }
    public SimuladorCooperativaService getMotor() { return motor; }
}