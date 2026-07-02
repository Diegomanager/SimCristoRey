package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.*;
import com.supermercado.domain.cooperativa.event.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SimuladorCooperativaService {

    // ── Configuracion ─────────────────────────────────────────────────────────
    private int    duracionSimuladaMinutos = 480;
    private long   msPorMinutoSimulado     = 62L;
    private int    maxSocios               = 400;
    private double intervaloLlegadaMinutos = 1.0;

    // ── Estado ────────────────────────────────────────────────────────────────
    private volatile boolean corriendo      = false;
    private volatile boolean pausado        = false;
    private volatile boolean faseRezagados  = false;
    private long tiempoSimuladoActual       = 0;

    // ── Entidades ─────────────────────────────────────────────────────────────
    private SalaEspera              salaEspera;
    private List<Caja>              cajas;
    private List<ServicioFinanciero> servicios;

    // ── Servicios ──────────────────────────────────────────────────────────────
    private final GeneradorSociosService    generador    = new GeneradorSociosService();
    private final AsignadorCajasService     asignador    = new AsignadorCajasService();
    private final EstadisticasFinancierasService estadisticas = new EstadisticasFinancierasService();

    // ── Listeners ─────────────────────────────────────────────────────────────
    private final List<Consumer<EventoSimulacion>> listeners = new CopyOnWriteArrayList<>();

    // ── Hilo ──────────────────────────────────────────────────────────────────
    private Thread hiloMotor;

    // =========================================================================
    // Configuracion
    // =========================================================================
    public void configurar(int duracionMinutos, long msPorMinuto, int maxSocios,
                           double intervaloLlegada, List<ServicioFinanciero> servicios,
                           List<Caja> cajas) {
        this.duracionSimuladaMinutos  = duracionMinutos;
        this.msPorMinutoSimulado      = Math.max(1L, msPorMinuto);
        this.maxSocios                = maxSocios;
        this.intervaloLlegadaMinutos  = intervaloLlegada;
        this.servicios                = servicios != null ? servicios : new ArrayList<>();
        this.cajas                    = cajas     != null ? cajas     : new ArrayList<>();
        this.salaEspera               = new SalaEspera();
        generador.setServicios(this.servicios);
    }

    public static long calcularEscala(int duracionSimuladaMinutos, int duracionRealSeg) {
        if (duracionSimuladaMinutos <= 0 || duracionRealSeg <= 0) return 62L;
        return Math.max(1L, (long) duracionRealSeg * 1000L / duracionSimuladaMinutos);
    }

    // =========================================================================
    // Control
    // =========================================================================
    public void iniciar() {
        if (corriendo) return;
        corriendo     = true;
        pausado       = false;
        faseRezagados = false;
        tiempoSimuladoActual = 0;
        generador.reiniciar();
        estadisticas.reiniciar();

        hiloMotor = new Thread(this::bucleMotor, "Motor-Cooperativa");
        hiloMotor.setDaemon(true);
        hiloMotor.start();
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_INICIADA, "Simulacion iniciada"));
    }

    public void pausar() {
        if (!corriendo || pausado) return;
        pausado = true;
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_PAUSADA, "Simulacion pausada"));
    }

    public void reanudar() {
        if (!corriendo || !pausado) return;
        pausado = false;
        synchronized (this) { notifyAll(); }
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_REANUDADA, "Simulacion reanudada"));
    }

    public void detener() {
        corriendo = false;
        pausado   = false;
        if (hiloMotor != null) {
            synchronized (this) { notifyAll(); }
            hiloMotor.interrupt();
        }
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_DETENIDA,
                "Simulacion detenida manualmente"));
    }

    public void reiniciar() {
        detener();
        if (salaEspera != null) salaEspera.reiniciar();
        if (cajas != null) cajas.forEach(Caja::reiniciar);
        generador.reiniciar();
        estadisticas.reiniciar();
        tiempoSimuladoActual = 0;
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_REINICIADA, "Simulacion reiniciada"));
    }

    // =========================================================================
    // Motor
    // =========================================================================
    private void bucleMotor() {
        double acumuladorLlegada = 0.0;

        while (corriendo) {
            if (pausado) {
                synchronized (this) {
                    while (pausado && corriendo) {
                        try { wait(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                    }
                }
                continue;
            }

            tiempoSimuladoActual++;

            if (!faseRezagados) {
                acumuladorLlegada += 1.0;
                while (acumuladorLlegada >= intervaloLlegadaMinutos
                        && generador.getTotalGenerados() < maxSocios) {
                    acumuladorLlegada -= intervaloLlegadaMinutos;
                    Socio s = generador.generarSocio(tiempoSimuladoActual);
                    salaEspera.agregarSocio(s);
                    publicar(new EventoSimulacion(TipoEvento.SOCIO_GENERADO,
                            "Socio " + s.getFicha() + " llego – " +
                            (s.getServicio() != null ? s.getServicio().getNombre() : "General")));
                }
            }

            procesarAsignaciones();
            procesarAtenciones();

            if (!faseRezagados && tiempoSimuladoActual >= duracionSimuladaMinutos) {
                publicar(new EventoSimulacion(TipoEvento.FASE_REZAGADOS_INICIADA,
                        "Tiempo cumplido. Rezagados en sala: " + salaEspera.getTotalEsperando()
                        + ". Iniciando fase de drenaje..."));
                faseRezagados = true;
            }

            if (faseRezagados && salaEspera.isEmpty()
                    && cajas.stream().noneMatch(c -> c.getEstado() == EstadoCaja.OCUPADA)) {
                corriendo = false;
                String motivo = generador.getTotalGenerados() >= maxSocios
                        ? "limite de socios alcanzado (" + maxSocios + ")"
                        : "tiempo de jornada cumplido (" + duracionSimuladaMinutos + " min)";
                publicar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                        "Simulacion finalizada por " + motivo +
                        ". Total atendidos: " + estadisticas.getTotalAtendidos()));
                break;
            }

            if (!faseRezagados && generador.getTotalGenerados() >= maxSocios) {
                publicar(new EventoSimulacion(TipoEvento.FASE_REZAGADOS_INICIADA,
                        "Limite de socios (" + maxSocios + ") alcanzado. Drenando sala..."));
                faseRezagados = true;
            }

            try {
                Thread.sleep(msPorMinutoSimulado);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void procesarAsignaciones() {
        while (!salaEspera.isEmpty()) {
            Socio siguiente = salaEspera.verSiguiente();
            if (siguiente == null) break;
            Optional<Caja> cajaOpt = asignador.asignar(siguiente, cajas);
            if (cajaOpt.isEmpty()) break;
            Socio socio = salaEspera.siguienteSocio();
            Caja  caja  = cajaOpt.get();
            asignador.confirmarAsignacion(socio, caja, tiempoSimuladoActual);
            publicar(new EventoSimulacion(TipoEvento.SOCIO_ASIGNADO,
                    "Socio " + socio.getFicha() + " asignado a " + caja.getId()
                    + " (" + caja.getTipo().getNombre() + ")"));
        }
    }

    private void procesarAtenciones() {
        for (Caja caja : cajas) {
            if (caja.getEstado() != EstadoCaja.OCUPADA) continue;
            Socio socio = caja.getSocioActual();
            if (socio == null) continue;

            long tiempoEnCaja = tiempoSimuladoActual - socio.getTiempoInicioAtencion();
            if (tiempoEnCaja >= socio.getDuracionEstimada()) {
                socio.setTiempoSalida(tiempoSimuladoActual);
                socio.setAtendida(true);
                estadisticas.registrarAtencion(socio, caja);
                caja.finalizarAtencion(tiempoSimuladoActual);
                publicar(new EventoSimulacion(TipoEvento.SOCIO_ATENDIDO,
                        "Socio " + socio.getFicha() + " atendido en " + caja.getId()
                        + " | Monto: Bs " + String.format("%.2f", socio.getMonto())
                        + " | Espera: " + (socio.getTiempoInicioAtencion() - socio.getTiempoLlegada()) + " min"));
            }
        }
    }

    // =========================================================================
    // Eventos
    // =========================================================================
    public void addListener(Consumer<EventoSimulacion> listener) { listeners.add(listener); }
    public void removeListener(Consumer<EventoSimulacion> listener) { listeners.remove(listener); }

    private void publicar(EventoSimulacion evento) {
        for (Consumer<EventoSimulacion> l : listeners) {
            try { l.accept(evento); } catch (Exception ignored) {}
        }
    }

    // =========================================================================
    // Getters
    // =========================================================================
    public boolean isCorriendo()     { return corriendo; }
    public boolean isPausado()       { return pausado; }
    public boolean isFaseRezagados() { return faseRezagados; }
    public long getTiempoSimulado()  { return tiempoSimuladoActual; }
    public SalaEspera getSalaEspera(){ return salaEspera; }
    public List<Caja> getCajas()     { return cajas; }
    public EstadisticasFinancierasService getEstadisticas() { return estadisticas; }
    public GeneradorSociosService getGenerador() { return generador; }
}