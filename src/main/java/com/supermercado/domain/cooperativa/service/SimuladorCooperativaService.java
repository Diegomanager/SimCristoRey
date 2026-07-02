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
    private volatile boolean corriendo          = false;
    private volatile boolean pausado            = false;
    private volatile boolean faseRezagados      = false;
    private volatile boolean fasePrincipalFinalizada = false;
    private long tiempoSimuladoActual           = 0;

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
        fasePrincipalFinalizada = false;
        tiempoSimuladoActual = 0;
        generador.reiniciar();
        estadisticas.reiniciar();

        hiloMotor = new Thread(this::bucleMotor, "Motor-Cooperativa");
        hiloMotor.setDaemon(true);
        hiloMotor.start();
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_INICIADA, "Simulacion iniciada", tiempoSimuladoActual));
    }

    // NUEVO: iniciar fase de rezagados (llamado desde UI)
    public void iniciarFaseRezagados() {
        if (!fasePrincipalFinalizada || faseRezagados) return;
        faseRezagados = true;
        publicar(new EventoSimulacion(TipoEvento.FASE_REZAGADOS_INICIADA,
                "Fase de rezagados iniciada. Socios en sala: " + salaEspera.getTotalEsperando(),
                tiempoSimuladoActual));
    }

    public void pausar() {
        if (!corriendo || pausado) return;
        pausado = true;
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_PAUSADA, "Simulacion pausada", tiempoSimuladoActual));
    }

    public void reanudar() {
        if (!corriendo || !pausado) return;
        pausado = false;
        synchronized (this) { notifyAll(); }
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_REANUDADA, "Simulacion reanudada", tiempoSimuladoActual));
    }

    public void detener() {
        corriendo = false;
        pausado   = false;
        if (hiloMotor != null) {
            synchronized (this) { notifyAll(); }
            hiloMotor.interrupt();
        }
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_DETENIDA,
                "Simulacion detenida manualmente", tiempoSimuladoActual));
    }

    public void reiniciar() {
        detener();
        if (salaEspera != null) salaEspera.reiniciar();
        if (cajas != null) cajas.forEach(Caja::reiniciar);
        generador.reiniciar();
        estadisticas.reiniciar();
        tiempoSimuladoActual = 0;
        fasePrincipalFinalizada = false;
        faseRezagados = false;
        publicar(new EventoSimulacion(TipoEvento.SIMULACION_REINICIADA, "Simulacion reiniciada", 0));
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

            if (!faseRezagados && !fasePrincipalFinalizada) {
                // FASE PRINCIPAL: generar socios
                acumuladorLlegada += 1.0;
                while (acumuladorLlegada >= intervaloLlegadaMinutos
                        && generador.getTotalGenerados() < maxSocios) {
                    acumuladorLlegada -= intervaloLlegadaMinutos;
                    Socio s = generador.generarSocio(tiempoSimuladoActual);
                    salaEspera.agregarSocio(s);
                    publicar(new EventoSimulacion(TipoEvento.SOCIO_GENERADO,
                            "Socio " + s.getFicha() + " llego – " +
                            (s.getServicio() != null ? s.getServicio().getNombre() : "General"),
                            tiempoSimuladoActual));
                }

                // Asignar socios a cajas libres (si hay)
                procesarAsignaciones();

                // Procesar atenciones
                procesarAtenciones();

                // Verificar fin de fase principal
                boolean tiempoCumplido = tiempoSimuladoActual >= duracionSimuladaMinutos;
                boolean limiteAlcanzado = generador.getTotalGenerados() >= maxSocios;

                if (tiempoCumplido || limiteAlcanzado) {
                    fasePrincipalFinalizada = true;
                    String motivo = limiteAlcanzado
                            ? "Limite de socios alcanzado (" + maxSocios + ")"
                            : "Tiempo de jornada cumplido (" + duracionSimuladaMinutos + " min)";
                    publicar(new EventoSimulacion(TipoEvento.FASE_PRINCIPAL_FINALIZADA,
                            motivo + ". Socios en sala: " + salaEspera.getTotalEsperando(),
                            tiempoSimuladoActual));
                    // No detenemos la simulación, esperamos que la UI decida si iniciar rezagados
                }

            } else if (faseRezagados) {
                // FASE DE REZAGADOS: solo atender socios pendientes
                procesarAsignaciones();
                procesarAtenciones();

                if (salaEspera.isEmpty() && cajas.stream().noneMatch(c -> c.getEstado() == EstadoCaja.OCUPADA)) {
                    corriendo = false;
                    publicar(new EventoSimulacion(TipoEvento.SIMULACION_FINALIZADA,
                            "Simulacion finalizada. Total atendidos: " + estadisticas.getTotalAtendidos() +
                            ". Rezagados: " + (generador.getTotalGenerados() - estadisticas.getTotalAtendidos()),
                            tiempoSimuladoActual));
                    break;
                }
            }

            // Esperar tick
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
                    + " (" + caja.getTipo().getNombre() + ")",
                    tiempoSimuladoActual));
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
                        + " | Espera: " + (socio.getTiempoInicioAtencion() - socio.getTiempoLlegada()) + " min",
                        tiempoSimuladoActual));
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
    public boolean isFasePrincipalFinalizada() { return fasePrincipalFinalizada; }
    public long getTiempoSimulado()  { return tiempoSimuladoActual; }
    public SalaEspera getSalaEspera(){ return salaEspera; }
    public List<Caja> getCajas()     { return cajas; }
    public EstadisticasFinancierasService getEstadisticas() { return estadisticas; }
    public GeneradorSociosService getGenerador() { return generador; }
}