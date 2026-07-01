package com.supermercado.domain.supermercado.service;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import com.supermercado.application.supermercado.dto.EstadisticasDTO;
import com.supermercado.application.supermercado.port.IEventPublisher;
import com.supermercado.application.supermercado.port.ILogService;
import com.supermercado.domain.supermercado.event.*;
import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        // ----- ESPERAR Y VACIAR COLAS (CON FORZADO) -----
        esperarYVaciarColas();

        detenerHilos();
        mostrarResultados();

        ejecutando = false;
        log("=== SIMULACION COMPLETADA ===");
    }

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
    // Metodos privados
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
        while (caja.estaActiva() && ejecutando) {
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
        cliente.setRapido(articulos <= config.getLimiteClienteRapido());
        cliente.setTiempoLlegada(reloj.getTiempoActual());
        String hora = calcularHoraSimulada((int) minutosSimuladosTranscurridos);
        log("[" + hora + "] Cliente-" + cliente.getId() +
            " generado | Articulos: " + articulos +
            (cliente.esRapido() ? " (RAPIDO)" : ""));
        eventPublisher.publish(new ClienteGeneradoEvent(cliente, hora));
        simuladorService.asignarCliente(cajas, cliente);
    }

    private void esperarYVaciarColas() throws InterruptedException {
        log("Esperando que terminen atenciones activas...");

        int esperaMax = 10000;
        int paso = 50;
        int intentos = esperaMax / paso;

        for (int i = 0; i < intentos; i++) {
            boolean hayOcupadas = false;
            for (Caja caja : cajas) {
                if (caja.estaOcupada()) {
                    hayOcupadas = true;
                    break;
                }
            }
            if (!hayOcupadas) {
                log("Todas las atenciones activas finalizadas naturalmente.");
                break;
            }
            Thread.sleep(paso);
        }

        int forzadas = 0;
        for (Caja caja : cajas) {
            if (caja.estaOcupada() && caja.getClienteActual() != null) {
                log("  Forzando finalización de " + caja.getId() +
                    " (cliente " + caja.getClienteActual().getId() + ")");
                caja.finalizarAtencion();
                forzadas++;
            }
        }
        if (forzadas > 0) {
            log("Atendidos forzados: " + forzadas);
        }

        log("Vaciando colas manualmente...");
        int totalEnCola = 0;
        for (Caja caja : cajas) {
            totalEnCola += caja.getClientesEnCola();
        }
        log("Clientes en cola al cierre: " + totalEnCola);

        int atendidosExtra = 0;
        for (Caja caja : cajas) {
            while (caja.getClientesEnCola() > 0 && ejecutando) {
                Cliente cliente = caja.prepararSiguienteCliente();
                if (cliente == null) break;
                cliente.setTiempoAtencionReal(1);
                cliente.setTiempoSalida(reloj.getTiempoActual() + 1);
                caja.finalizarAtencion();
                atendidosExtra++;
                log("  Cola de " + caja.getId() + " atendido cliente " + cliente.getId());
            }
        }
        if (atendidosExtra > 0) {
            log("Atendidos extra en cola: " + atendidosExtra);
        }

        int atendidosTotales = 0;
        for (Caja caja : cajas) {
            atendidosTotales += caja.getTotalAtendidos();
        }
        int perdidos = clientesGenerados - atendidosTotales - totalEnCola;
        if (perdidos > 0) {
            log("ADVERTENCIA: Clientes perdidos: " + perdidos);
        } else {
            log("Todos los clientes generados fueron contabilizados.");
        }
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

        eventPublisher.publish(new SimulacionFinalizadaEvent(completo));

        log("\n============ ESTADISTICAS FINALES ============");
        for (Caja caja : cajas) {
            log(String.format("  %s: %d clientes | Cola maxima: %d | %s",
                caja.getId(), caja.getTotalAtendidos(), caja.getColaMaxima(),
                caja.esRapida() ? "RAPIDA" : "NORMAL"));
        }
        log("----------------------------------------------");
        log("  Total clientes atendidos: " + completo.getTotalClientesAtendidos());
        log("  Total articulos vendidos: " + completo.getTotalArticulosVendidos());
        log("  Total minutos de atencion: " + completo.getTotalMinutosAtencion());
        log("  Clientes generados: " + completo.getClientesGenerados());
        log(String.format("  Prom. articulos/cliente: %.2f", completo.getArticulosPromedio()));
        log(String.format("  Prom. minutos/cliente: %.2f", completo.getTiempoPromedioAtencion()));
        log("  Cajero Estrella: " + completo.getCajeroEstrella());
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