package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.config.ConfiguracionCooperativa;
import com.supermercado.domain.cooperativa.event.EventoSimulacion;
import com.supermercado.domain.cooperativa.event.TipoEvento;
import com.supermercado.domain.cooperativa.model.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SimuladorCooperativaService {

    private long msPorMinuto = 62L;
    private int maxSociosDia = 200;
    private double intervaloMinutos = 1.0;
    private int diaActual = 1;
    private int diaSimulado = 0;

    private volatile boolean corriendo = false;
    private volatile boolean pausado = false;
    private volatile boolean faseRezagados = false;
    private volatile boolean fasePrincipalFinalizada = false;

    private volatile long tiempoReloj = 510;
    private volatile long tiempoMotor = 0;

    private SalaEspera salaEspera;
    private List<Caja> cajas;
    private List<ServicioFinanciero> servicios;
    private JornadaLaboral jornadaActual;

    private final GeneradorSociosService generador = new GeneradorSociosService();
    private final AsignadorCajasService asignador = new AsignadorCajasService();
    private final EstadisticasFinancierasService estadisticas = new EstadisticasFinancierasService();

    private final List<Consumer<EventoSimulacion>> listeners = new CopyOnWriteArrayList<>();
    private Thread hiloMotor;

    private List<Socio> sociosPredefinidos;
    private boolean modoReplay = false;

    public void setSociosPredefinidos(List<Socio> lista) {
        this.sociosPredefinidos = lista;
        this.modoReplay = lista != null && !lista.isEmpty();
    }

    public boolean isModoReplay() {
        return modoReplay;
    }

    public void configurar(long msPorMin, int maxSoc, double intv,
                           List<ServicioFinanciero> svcs, List<Caja> cajasLista,
                           ConfiguracionMultiServicio multi,
                           ConfiguracionCooperativa cfg) {
        this.msPorMinuto = Math.max(1L, msPorMin);
        this.maxSociosDia = maxSoc;
        this.intervaloMinutos = intv;
        this.servicios = svcs != null ? svcs : new ArrayList<>();
        this.cajas = cajasLista != null ? cajasLista : new ArrayList<>();
        this.salaEspera = new SalaEspera();
        generador.setServicios(this.servicios);
        generador.setConfiguracionMultiServicio(multi != null ? multi : new ConfiguracionMultiServicio());
        if (cfg != null) generador.setConfiguracion(cfg);
    }

    public static long calcularEscala(double horasSimuladas, int durRealSeg) {
        int min = (int) (horasSimuladas * 60);
        if (min <= 0 || durRealSeg <= 0) return 62L;
        return Math.max(1L, (long) durRealSeg * 1000L / min);
    }

    public void setDiaSimulado(int dia) {
        this.diaSimulado = dia;
    }

    public int getDiaSimulado() {
        return diaSimulado;
    }

    public void iniciar(JornadaLaboral jornada, int dia) {
        if (corriendo) return;
        this.jornadaActual = jornada;
        this.diaActual = dia;
        corriendo = true;
        pausado = false;
        faseRezagados = false;
        fasePrincipalFinalizada = false;
        tiempoReloj = jornada != null ? jornada.getMinutoInicio() : 510;
        tiempoMotor = 0;
        generador.reiniciar();
        estadisticas.reiniciar();

        hiloMotor = new Thread(this::bucleMotor, "Motor-Dia-" + dia);
        hiloMotor.setDaemon(true);
        hiloMotor.start();
        String etiqueta = modoReplay ? " (replay)" : "";
        publicar(TipoEvento.SIMULACION_INICIADA, "=== Dia " + diaSimulado + " iniciado" + etiqueta + " - " + horaSimulada() + " ===");
    }

    public void pausar() {
        if (!corriendo || pausado) return;
        pausado = true;
        publicar(TipoEvento.SIMULACION_PAUSADA, "Pausado");
    }

    public void reanudar() {
        if (!corriendo || !pausado) return;
        pausado = false;
        synchronized (this) {
            notifyAll();
        }
        publicar(TipoEvento.SIMULACION_REANUDADA, "Reanudado");
    }

    public void detener() {
        corriendo = false;
        pausado = false;
        if (hiloMotor != null) {
            synchronized (this) {
                notifyAll();
            }
            hiloMotor.interrupt();
        }
        publicar(TipoEvento.SIMULACION_DETENIDA, "Detenido");
    }

    public void reiniciarDia() {
        detener();
        if (salaEspera != null) salaEspera.reiniciar();
        if (cajas != null) cajas.forEach(Caja::reiniciar);
        generador.reiniciar();
        estadisticas.reiniciar();
        tiempoReloj = jornadaActual != null ? jornadaActual.getMinutoInicio() : 510;
        tiempoMotor = 0;
        fasePrincipalFinalizada = false;
        faseRezagados = false;
        publicar(TipoEvento.SIMULACION_REINICIADA, "Reiniciado");
    }

    public void iniciarFaseRezagados() {
        if (!fasePrincipalFinalizada || faseRezagados) return;
        faseRezagados = true;
        corriendo = true;
        estadisticas.setFaseRezagados(true);
        hiloMotor = new Thread(this::bucleRezagados, "Motor-Rez-" + diaActual);
        hiloMotor.setDaemon(true);
        hiloMotor.start();
        publicar(TipoEvento.FASE_REZAGADOS_INICIADA, "Drenando sala: " + salaEspera.getTotalEsperando() + " socios");
    }

    private void bucleMotor() {
        if (modoReplay) {
            if (sociosPredefinidos != null && !sociosPredefinidos.isEmpty()) {
                bucleMotorReplay();
            } else {
                System.out.println(">>> [Replay] Sin socios predefinidos. Día finalizado sin generación.");
                fasePrincipalFinalizada = true;
                corriendo = false;
            }
            return;
        }

        // MODO MANUAL
        double acum = 0.0;
        List<BloqueHorario> bloques = jornadaActual.getBloques();
        if (bloques.isEmpty()) {
            fasePrincipalFinalizada = true;
            corriendo = false;
            publicar(TipoEvento.FASE_PRINCIPAL_FINALIZADA, "Sin bloques horarios");
            return;
        }

        for (BloqueHorario bloque : bloques) {
            if (!corriendo) break;
            int inicio = bloque.getInicio();
            int fin = bloque.getFin();
            tiempoReloj = inicio;
            for (int minuto = inicio; minuto < fin; minuto++) {
                if (!corriendo || fasePrincipalFinalizada) break;
                if (pausado) {
                    synchronized (this) {
                        while (pausado && corriendo) {
                            try {
                                wait(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                    if (!corriendo) break;
                }

                tiempoMotor++;
                tiempoReloj = minuto;

                acum += 1.0;
                while (acum >= intervaloMinutos && generador.getTotalGenerados() < maxSociosDia) {
                    acum -= intervaloMinutos;
                    Socio s = generador.generarSocio(tiempoMotor);
                    salaEspera.agregarSocio(s);
                    estadisticas.setTotalGenerados(generador.getTotalGenerados());
                    publicar(TipoEvento.SOCIO_GENERADO, s.getFicha() + " llego - " + s.getDescripcionServicios());
                }

                procesarAsignaciones();
                procesarAtenciones();
                sleep();
            }
        }

        fasePrincipalFinalizada = true;
        corriendo = false;
        int enSala = salaEspera.getTotalEsperando();
        int cajasOcupadas = (int) cajas.stream().filter(c -> c.getEstado() == EstadoCaja.OCUPADA).count();
        publicar(TipoEvento.FASE_PRINCIPAL_FINALIZADA, "Fase principal terminada | En sala: " + enSala + " | En cajas: " + cajasOcupadas + " | Atendidos: " + estadisticas.getPrincipal().getTotalAtendidos());
    }

    private void bucleMotorReplay() {
        List<BloqueHorario> bloques = jornadaActual.getBloques();
        int inicioJornada = bloques.isEmpty() ? (int) tiempoReloj : bloques.get(0).getInicio();
        int finJornada = bloques.isEmpty() ? 24 * 60 : bloques.get(bloques.size() - 1).getFin();

        List<Socio> pendientes = new ArrayList<>(sociosPredefinidos);
        pendientes.sort(Comparator.comparingLong(Socio::getTiempoLlegada));

        int idx = 0;
        tiempoReloj = inicioJornada;

        for (int minuto = inicioJornada; minuto < finJornada; minuto++) {
            if (!corriendo || fasePrincipalFinalizada) break;
            if (pausado) {
                synchronized (this) {
                    while (pausado && corriendo) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                if (!corriendo) break;
            }

            tiempoMotor++;
            tiempoReloj = minuto;

            while (idx < pendientes.size() && pendientes.get(idx).getTiempoLlegada() <= minuto) {
                Socio s = pendientes.get(idx++);
                salaEspera.agregarSocio(s);
                estadisticas.setTotalGenerados(idx);
                publicar(TipoEvento.SOCIO_GENERADO, s.getFicha() + " llego (replay) - " + s.getDescripcionServicios());
            }

            procesarAsignaciones();
            procesarAtenciones();
            sleep();
        }

        while (idx < pendientes.size() && corriendo) {
            Socio s = pendientes.get(idx++);
            salaEspera.agregarSocio(s);
            estadisticas.setTotalGenerados(idx);
        }

        fasePrincipalFinalizada = true;
        corriendo = false;
        int enSala = salaEspera.getTotalEsperando();
        int cajasOcupadas = (int) cajas.stream().filter(c -> c.getEstado() == EstadoCaja.OCUPADA).count();
        publicar(TipoEvento.FASE_PRINCIPAL_FINALIZADA, "Fase principal (replay) terminada | En sala: " + enSala + " | En cajas: " + cajasOcupadas + " | Atendidos: " + estadisticas.getPrincipal().getTotalAtendidos());
    }

    private void bucleRezagados() {
        int extraSeguridad = 0;
        while (corriendo) {
            if (pausado) {
                synchronized (this) {
                    while (pausado && corriendo) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                if (!corriendo) break;
            }

            tiempoMotor++;
            tiempoReloj++;
            if (tiempoReloj >= 24 * 60) tiempoReloj -= 24 * 60;

            procesarAtenciones();
            procesarAsignaciones();

            boolean salaVacia = salaEspera.isEmpty();
            boolean cajasLibres = cajas.stream().noneMatch(c -> c.getEstado() == EstadoCaja.OCUPADA);

            if (salaVacia && cajasLibres) {
                corriendo = false;
                publicar(TipoEvento.SIMULACION_FINALIZADA, "\u2705 Dia " + diaSimulado + " completo | Total atendidos: " + estadisticas.getTotalAtendidos() + " | Monto: Bs " + String.format("%.2f", estadisticas.getMontoTotal()));
                break;
            }
            extraSeguridad++;
            if (extraSeguridad > maxSociosDia * 60) {
                corriendo = false;
                publicar(TipoEvento.SIMULACION_FINALIZADA, "\u26A0\uFE0F Dia " + diaSimulado + " - tiempo de drenaje agotado | Atendidos: " + estadisticas.getTotalAtendidos());
                break;
            }
            sleep();
        }
    }

    private void procesarAsignaciones() {
        while (!salaEspera.isEmpty()) {
            Socio siguiente = salaEspera.verSiguiente();
            if (siguiente == null) break;
            Optional<Caja> opt = asignador.asignar(siguiente, cajas);
            if (opt.isEmpty()) break;
            Socio s = salaEspera.siguienteSocio();
            Caja c = opt.get();
            asignador.confirmarAsignacion(s, c, tiempoMotor);
            publicar(TipoEvento.SOCIO_ASIGNADO, s.getFicha() + " -> " + c.getId() + (s.isMultiServicio() ? " [x" + s.getTotalServicios() + "]" : ""));
        }
    }

    private void procesarAtenciones() {
        for (Caja caja : cajas) {
            if (caja.getEstado() != EstadoCaja.OCUPADA) continue;
            Socio s = caja.getSocioActual();
            if (s == null) continue;
            long enCaja = tiempoMotor - s.getTiempoInicioAtencion();
            if (enCaja >= s.getDuracionEstimada()) {
                s.setTiempoSalida(tiempoMotor);
                s.setAtendida(true);
                estadisticas.registrarAtencion(s, caja);
                caja.finalizarAtencion(tiempoMotor);
                long espera = s.getTiempoInicioAtencion() - s.getTiempoLlegada();
                publicar(TipoEvento.SOCIO_ATENDIDO, s.getFicha() + " \u2713 " + caja.getId() + " | Espera: " + Math.max(0, espera) + " min | Aten: " + s.getDuracionEstimada() + " min | Bs " + String.format("%.0f", s.getMonto()));
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(msPorMinuto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String horaSimulada() {
        int m = (int) (tiempoReloj % (24 * 60));
        return String.format("D\u00eda %d \u2013 %02d:%02d", diaSimulado, m / 60, m % 60);
    }

    public ResumenDiario construirResumenDia() {
        ResumenDiario r = new ResumenDiario(diaActual, true);
        r.setNumeroDia(diaSimulado);
        r.setGenerados(!modoReplay
                ? generador.getTotalGenerados()
                : (sociosPredefinidos != null ? sociosPredefinidos.size() : 0));
        r.setAtendidosPrincipal(estadisticas.getPrincipal().getTotalAtendidos());
        r.setAtendidosRezagados(estadisticas.getRezagados().getTotalAtendidos());
        r.setNoAtendidos(Math.max(0, r.getGenerados() - r.getTotalAtendidos()));
        r.setMontoTotal(estadisticas.getMontoTotal());
        r.setPromedioEspera(estadisticas.getPromedioEspera());
        r.setPromedioAtencion(estadisticas.getPromedioAtencion());
        r.setCajeroEstrella(estadisticas.getCajeroEstrellaGlobal());
        int min = jornadaActual != null ? jornadaActual.getTotalMinutosLaborables() : 480;
        r.setEficienciaGlobal(min > 0 ? (double) r.getTotalAtendidos() / min : 0);

        Map<String, Integer> desglose = new LinkedHashMap<>(estadisticas.getPrincipal().getAtendidosPorCodigo());
        estadisticas.getRezagados().getAtendidosPorCodigo().forEach((k, v) -> desglose.merge(k, v, Integer::sum));
        r.setAtendidosPorServicio(desglose);

        return r;
    }

    public void addListener(Consumer<EventoSimulacion> l) {
        listeners.add(l);
    }

    public void removeListener(Consumer<EventoSimulacion> l) {
        listeners.remove(l);
    }

    private void publicar(TipoEvento t, String msg) {
        EventoSimulacion ev = new EventoSimulacion(t, msg, horaSimulada());
        for (var l : listeners) {
            try {
                l.accept(ev);
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isCorriendo() {
        return corriendo;
    }

    public boolean isPausado() {
        return pausado;
    }

    public boolean isFaseRezagados() {
        return faseRezagados;
    }

    public boolean isFasePrincipalFinalizada() {
        return fasePrincipalFinalizada;
    }

    public long getTiempoReloj() {
        return tiempoReloj;
    }

    public long getTiempoMotor() {
        return tiempoMotor;
    }

    public int getDiaActual() {
        return diaActual;
    }

    public SalaEspera getSalaEspera() {
        return salaEspera;
    }

    public List<Caja> getCajas() {
        return cajas;
    }

    public EstadisticasFinancierasService getEstadisticas() {
        return estadisticas;
    }

    public GeneradorSociosService getGenerador() {
        return generador;
    }

    public JornadaLaboral getJornadaActual() {
        return jornadaActual;
    }
}