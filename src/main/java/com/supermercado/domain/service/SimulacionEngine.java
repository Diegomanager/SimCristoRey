package com.supermercado.domain.service;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.application.port.IEventPublisher;
import com.supermercado.application.port.ILogService;
import com.supermercado.domain.event.*;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Motor de simulacion del supermercado.
 *
 * Responsabilidad unica: ejecutar la logica de simulacion del dominio.
 * Publica eventos para comunicar cambios de estado a la infraestructura.
 *
 * NOTA sobre sincronizacion:
 * - iniciar() NO es synchronized porque su bucle principal debe
 *   ceder el control a pausar()/reanudar() que si son synchronized.
 * - pausado y ejecutando son volatile para visibilidad entre hilos.
 */
public class SimulacionEngine {

    private static final int HORA_APERTURA         = 8;
    private static final int CAJA_UPDATE_INTERVAL  = 5;
    private static final int STATS_UPDATE_INTERVAL = 3;

    private final ILogService      logService;
    private final IEventPublisher  eventPublisher;
    private final RelojSimulacionService reloj;
    private final SimuladorService       simuladorService;
    private final EstadisticasService    estadisticasService;
    private final Random random;

    private ConfiguracionDTO   config;
    private List<Caja>         cajas;
    private List<Thread>       hilosCajas;
    private volatile boolean   ejecutando;
    private volatile boolean   pausado;
    private int                clientesGenerados;
    private long               tiempoInicio;
    private long               tiempoPausaInicio;
    private long               msPerMinutoSimulado;
    private int                minSimulados;
    private volatile long      minutosSimuladosTranscurridos;

    public SimulacionEngine(ILogService logService, IEventPublisher eventPublisher) {
        this.logService          = logService;
        this.eventPublisher      = eventPublisher;
        this.reloj               = new RelojSimulacionService();
        this.simuladorService    = new SimuladorService(reloj);
        this.estadisticasService = new EstadisticasService();
        this.random              = new Random();
        this.cajas               = new ArrayList<>();
        this.hilosCajas          = new ArrayList<>();
        this.ejecutando          = false;
        this.pausado             = false;
        this.clientesGenerados   = 0;
        this.minutosSimuladosTranscurridos = 0;
        this.tiempoPausaInicio   = 0;
    }

    // ============================================================
    // SIN synchronized - el bucle principal cede el lock
    // para que pausar()/reanudar() puedan ejecutarse
    // ============================================================
    public void iniciar(ConfiguracionDTO config) throws InterruptedException {
        if (ejecutando) {
            throw new IllegalStateException("La simulacion ya esta en ejecucion");
        }
        this.config                        = config;
        this.ejecutando                    = true;
        this.pausado                       = false;
        this.clientesGenerados             = 0;
        this.minutosSimuladosTranscurridos = 0;
        this.cajas.clear();
        this.hilosCajas.clear();

        log("=== INICIANDO SIMULACION ===");
        log("Horario: 08:00 - " + (HORA_APERTURA + config.getHorasSimuladas()) +
            ":00 (" + config.getHorasSimuladas() + " horas)");
        log("Duracion real: " + config.getDuracionRealSegundos() +
            "s | Tiempo simulado: " + config.getHorasSimuladas() + "h");
        log("Cajas Normales: " + config.getNumCajasNormales() +
            " | Cajas Rapidas: " + config.getNumCajasRapidas());

        inicializarCajas();
        iniciarHilosCajas();
        publicarEstadisticas();
        ejecutarCicloPrincipal();
        esperarFinalizacionAtenciones();
        detenerHilos();
        mostrarResultados();

        ejecutando = false;
        log("=== SIMULACION COMPLETADA ===");
    }

    // synchronized solo en metodos cortos que cambian estado
    public synchronized void pausar() {
        if (!ejecutando || pausado) return;
        pausado           = true;
        tiempoPausaInicio = System.currentTimeMillis();
        reloj.pausar();
        for (Caja caja : cajas) caja.pausar();
        eventPublisher.publish(new SimulacionPausadaEvent());
        log("Simulacion pausada");
    }

    public synchronized void reanudar() {
        if (!ejecutando || !pausado) return;
        tiempoInicio  += (System.currentTimeMillis() - tiempoPausaInicio);
        tiempoPausaInicio = 0;
        pausado           = false;
        reloj.reanudar();
        for (Caja caja : cajas) caja.reanudar();
        eventPublisher.publish(new SimulacionReanudadaEvent());
        log("Simulacion reanudada");
    }

    public synchronized void detener() {
        ejecutando        = false;
        pausado           = false;
        tiempoPausaInicio = 0;
        for (Caja caja : cajas) caja.detener();
        reloj.detener();
        eventPublisher.publish(new SimulacionDetenidaEvent());
        log("Simulacion detenida");
    }

    public boolean isEjecutando() { return ejecutando; }
    public boolean isPausado()    { return pausado; }

    public EstadisticasDTO obtenerEstadisticas() {
        return estadisticasService.calcularEstadisticas(cajas);
    }

    // ============================================================
    // Metodos privados - cada uno con responsabilidad clara
    // ============================================================

    private void inicializarCajas() {
        int numRapidas  = config.getNumCajasRapidas();
        int numNormales = config.getNumCajasNormales();
        for (int i = 1; i <= numRapidas;  i++) cajas.add(new Caja(i, true));
        for (int i = 1; i <= numNormales; i++) cajas.add(new Caja(numRapidas + i, false));
        log("Total cajas creadas: " + cajas.size());
    }

    private void iniciarHilosCajas() {
        for (Caja caja : cajas) {
            Thread hilo = new Thread(() -> ejecutarCaja(caja));
            hilo.setName("hilo-caja-" + caja.getId());
            hilo.start();
            hilosCajas.add(hilo);
        }
    }

    private void ejecutarCaja(Caja caja) {
        while (caja.estaActiva()) {
            try {
                while (pausado && ejecutando) Thread.sleep(50);
                if (!ejecutando) break;

                Cliente cliente = caja.prepararSiguienteCliente();
                if (cliente == null) {
                    Thread.sleep(Math.max(10, msPerMinutoSimulado / 2));
                    continue;
                }

                int tMin = caja.esRapida()
                    ? config.getTiempoCajaRapidaMin() : config.getTiempoCajaNormalMin();
                int tMax = caja.esRapida()
                    ? config.getTiempoCajaRapidaMax() : config.getTiempoCajaNormalMax();

                int tiempoAtencion = tMin + random.nextInt(Math.max(1, tMax - tMin + 1));
                cliente.setTiempoAtencionReal(tiempoAtencion);

                String hora = calcularHoraSimulada((int) minutosSimuladosTranscurridos);
                log("[" + hora + "] Atendiendo: " + cliente.getId() +
                    " en " + caja.getId() + " (" + tiempoAtencion + " min)");

                Thread.sleep(Math.max(50, tiempoAtencion * msPerMinutoSimulado));

                cliente.setTiempoSalida(reloj.getTiempoActual());
                caja.finalizarAtencion();
                publicarCajaActualizada(caja);
                log("Finalizada atencion de " + cliente.getId() + " en " + caja.getId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void ejecutarCicloPrincipal() throws InterruptedException {
        minSimulados        = config.getHorasSimuladas() * 60;
        int  duracionRealMs = config.getDuracionRealSegundos() * 1000;
        msPerMinutoSimulado = Math.max(1, duracionRealMs / minSimulados);
        long tickMs         = Math.max(10, msPerMinutoSimulado / 2);
        long duracionMs     = config.getDuracionRealSegundos() * 1000L;
        int  limite         = config.getLimiteClientes();

        log("Escala: 1 min simulado = " + msPerMinutoSimulado + "ms reales");

        reloj.iniciar();
        tiempoInicio = System.currentTimeMillis();
        long tiempoTranscurrido = 0;
        int  ticks = 0;

        while (ejecutando && tiempoTranscurrido < duracionMs) {
            while (pausado && ejecutando) Thread.sleep(50);
            if (!ejecutando) break;

            tiempoTranscurrido             = System.currentTimeMillis() - tiempoInicio;
            minutosSimuladosTranscurridos  = (tiempoTranscurrido * minSimulados) / duracionMs;
            reloj.setTiempoSimulado(minutosSimuladosTranscurridos);
            ticks++;

            if (estaCerrado()) {
                log("CIERRE DEL SUPERMERCADO (" +
                    (HORA_APERTURA + config.getHorasSimuladas()) + ":00)");
                break;
            }
            if (limite > 0 && clientesGenerados >= limite) {
                log("Limite de " + limite + " clientes alcanzado.");
                break;
            }
            if (random.nextInt(100) < config.getProbabilidadLlegadaCliente()) {
                generarCliente();
            }
            if (ticks % CAJA_UPDATE_INTERVAL  == 0) publicarTodasLasCajas();
            if (ticks % STATS_UPDATE_INTERVAL == 0) publicarEstadisticas();

            Thread.sleep(tickMs);
        }
    }

        private void generarCliente() {
        int min       = config.getArticulosClienteMin();
        int max       = config.getArticulosClienteMax();
        int articulos = min + random.nextInt(max - min + 1);
        Cliente cliente = new Cliente(++clientesGenerados, articulos);
        // Determinar si es rápido según límite configurado
        cliente.setRapido(articulos <= config.getLimiteClienteRapido());
        cliente.setTiempoLlegada(reloj.getTiempoActual());
        String hora = calcularHoraSimulada((int) minutosSimuladosTranscurridos);
        log("[" + hora + "] Cliente-" + cliente.getId() +
            " generado | Articulos: " + articulos +
            (cliente.esRapido() ? " (RÁPIDO)" : ""));
        eventPublisher.publish(new ClienteGeneradoEvent(cliente, hora));
        simuladorService.asignarCliente(cajas, cliente);
    }

    private void esperarFinalizacionAtenciones() throws InterruptedException {
        log("Esperando que terminen las atenciones...");
        int intentos = 0;
        boolean hayPendientes;
        do {
            hayPendientes = cajas.stream()
                .anyMatch(c -> c.tieneClientesPendientes() || c.estaOcupada());
            if (hayPendientes) {
                Thread.sleep(Math.max(10, msPerMinutoSimulado / 2));
                if (++intentos > 200) break;
            }
        } while (hayPendientes && ejecutando);
    }

    private void detenerHilos() {
        for (Caja caja : cajas) caja.detener();
        reloj.detener();
        ejecutando = false;
        for (Thread h : hilosCajas) {
            if (h.isAlive()) h.interrupt();
        }
    }

    private void mostrarResultados() {
        EstadisticasDTO stats = estadisticasService.calcularEstadisticas(cajas);
        eventPublisher.publish(new SimulacionFinalizadaEvent(stats));

        log("\n============ ESTADISTICAS FINALES ============");
        for (Caja caja : cajas) {
            log(String.format("  %s: %d clientes | Cola maxima: %d | %s",
                caja.getId(), caja.getTotalAtendidos(), caja.getColaMaxima(),
                caja.esRapida() ? "RAPIDA" : "NORMAL"));
        }
        log("----------------------------------------------");
        log("  Total clientes atendidos: " + stats.getTotalClientesAtendidos());
        log("  Total articulos vendidos: " + stats.getTotalArticulosVendidos());
        log("  Total minutos de atencion: " + stats.getTotalMinutosAtencion());
        log("  Clientes generados: " + clientesGenerados);
        log(String.format("  Prom. articulos/cliente: %.2f", stats.getArticulosPromedio()));
        log(String.format("  Prom. minutos/cliente: %.2f", stats.getTiempoPromedioAtencion()));
        log("  Cajero Estrella: " + stats.getCajeroEstrella());
        log("==============================================\n");
    }

    private void publicarTodasLasCajas() {
        for (Caja caja : cajas) publicarCajaActualizada(caja);
    }

    private void publicarCajaActualizada(Caja caja) {
        String numStr = caja.getId().replaceAll("\\D+", "");
        if (numStr.isEmpty()) return;
        int    num    = Integer.parseInt(numStr);
        String estado = caja.estaOcupada() ? "ATENDIENDO" : "LIBRE";
        if (caja.getEstado().toString().equals("PAUSADA")) estado = "PAUSADA";
        String clienteInfo = caja.getClienteActual() != null
            ? caja.getClienteActual().getId() : "";
        String tipo = caja.esRapida() ? "R" : "N";
        eventPublisher.publish(new CajaActualizadaEvent(
            num, estado, caja.getClientesEnCola(), clienteInfo, tipo));
    }

    private void publicarEstadisticas() {
        EstadisticasDTO stats = estadisticasService.calcularEstadisticas(cajas);
        String hora = calcularHoraSimulada((int) minutosSimuladosTranscurridos);
        EstadisticasDTO completo = new EstadisticasDTO.Builder()
            .totalClientesAtendidos(stats.getTotalClientesAtendidos())
            .totalArticulosVendidos(stats.getTotalArticulosVendidos())
            .totalMinutosAtencion(stats.getTotalMinutosAtencion())
            .clientesEnCola(stats.getClientesEnCola())
            .cajeroEstrella(stats.getCajeroEstrella())
            .tiempoPromedioAtencion(stats.getTiempoPromedioAtencion())
            .articulosPromedio(stats.getArticulosPromedio())
            .clientesGenerados(clientesGenerados)
            .horaSimulada(hora)
            .build();
        eventPublisher.publish(new EstadisticasActualizadasEvent(completo));
    }

    private boolean estaCerrado() {
        return minutosSimuladosTranscurridos >= (long)(config.getHorasSimuladas() * 60);
    }

    private String calcularHoraSimulada(int minutos) {
        int total = config.getHorasSimuladas() * 60;
        minutos   = Math.max(0, Math.min(minutos, total));
        int hora  = (HORA_APERTURA + (minutos / 60)) % 24;
        int min   = minutos % 60;
        return String.format("%02d:%02d", hora, min);
    }

    private void log(String mensaje) {
        logService.info(mensaje);
    }
}