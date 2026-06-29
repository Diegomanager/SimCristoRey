package com.supermercado.infrastructure.adapter.ui;

import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.application.port.ILogService;
import com.supermercado.domain.event.*;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;
import com.supermercado.presentation.view.SimuladorFrame;

import javax.swing.SwingUtilities;

/**
 * Adaptador de infraestructura: escucha eventos del dominio
 * y actualiza la interfaz gráfica de usuario.
 *
 * Responsabilidad única: traducir eventos de dominio a
 * llamadas sobre la vista Swing, siempre en el EDT.
 *
 * No contiene lógica de negocio.
 * No usa System.out.println (usa ILogService).
 */
public class SwingEventAdapter {

    private final SimuladorFrame  frame;
    private final EventBusAdapter eventBus;
    private final ILogService     logService;

    public SwingEventAdapter(
            SimuladorFrame frame,
            EventBusAdapter eventBus,
            ILogService logService) {
        this.frame      = frame;
        this.eventBus   = eventBus;
        this.logService = logService;
        suscribirEventos();
        logService.debug("SwingEventAdapter suscrito a eventos del dominio");
    }

    // ============================================================
    // Suscripciones - un método por evento
    // ============================================================

    private void suscribirEventos() {
        eventBus.subscribe(ClienteGeneradoEvent.class,          this::onClienteGenerado);
        eventBus.subscribe(CajaActualizadaEvent.class,          this::onCajaActualizada);
        eventBus.subscribe(EstadisticasActualizadasEvent.class, this::onEstadisticasActualizadas);
        eventBus.subscribe(SimulacionFinalizadaEvent.class,     this::onSimulacionFinalizada);
        eventBus.subscribe(SimulacionPausadaEvent.class,        this::onSimulacionPausada);
        eventBus.subscribe(SimulacionReanudadaEvent.class,      this::onSimulacionReanudada);
        eventBus.subscribe(SimulacionDetenidaEvent.class,       this::onSimulacionDetenida);
        eventBus.subscribe(SimulacionIniciadaEvent.class,       this::onSimulacionIniciada);
    }

    // ============================================================
    // Manejadores de eventos - todos en el EDT via invokeLater
    // ============================================================

    private void onClienteGenerado(ClienteGeneradoEvent event) {
        SwingUtilities.invokeLater(() ->
            frame.agregarLog("[" + event.getHora() + "] Cliente-" +
                event.getCliente().getId() +
                " generado | Artículos: " +
                event.getCliente().getCantidadArticulos())
        );
    }

    private void onCajaActualizada(CajaActualizadaEvent event) {
        SwingUtilities.invokeLater(() ->
            frame.actualizarCajaConTipo(
                event.getNumCaja(),
                event.getEstado(),
                event.getCola(),
                event.getClienteInfo(),
                event.getTipo())
        );
    }

    private void onEstadisticasActualizadas(EstadisticasActualizadasEvent event) {
        SwingUtilities.invokeLater(() ->
            frame.actualizarEstadisticas(event.getEstadisticas())
        );
    }

    private void onSimulacionFinalizada(SimulacionFinalizadaEvent event) {
        logService.debug("Evento SimulacionFinalizada recibido");
        SwingUtilities.invokeLater(() -> {
            EstadisticasDTO stats = event.getEstadisticas();
            frame.actualizarEstadisticas(stats);
            frame.agregarLog("=== SIMULACION COMPLETADA ===");
            frame.agregarLog("=== ESTADÍSTICAS FINALES ===");
            frame.agregarLog("Total clientes atendidos: " + stats.getTotalClientesAtendidos());
            frame.agregarLog("Total artículos vendidos: " + stats.getTotalArticulosVendidos());
            frame.agregarLog("Total minutos de atención: " + stats.getTotalMinutosAtencion());
            frame.agregarLog("Clientes en cola al final: " + stats.getClientesEnCola());
            frame.agregarLog("Cajero estrella: " + stats.getCajeroEstrella());
            frame.agregarLog(String.format("Promedio artículos/cliente: %.2f", stats.getArticulosPromedio()));
            frame.agregarLog(String.format("Promedio minutos/cliente: %.2f", stats.getTiempoPromedioAtencion()));
            frame.agregarLog("=================================");
            frame.mostrarMensaje("Simulación completada");
            frame.habilitarBotonIniciar(true);
            frame.habilitarBotonDetener(false);
            frame.cambiarEstadoPausa(false);
        });
    }

    private void onSimulacionIniciada(SimulacionIniciadaEvent event) {
        logService.debug("Evento SimulacionIniciada recibido");
        SwingUtilities.invokeLater(() -> {
            frame.habilitarBotonIniciar(false);
            frame.habilitarBotonDetener(true);
        });
    }

    private void onSimulacionPausada(SimulacionPausadaEvent event) {
        logService.debug("Evento SimulacionPausada recibido");
        SwingUtilities.invokeLater(() -> {
            frame.cambiarEstadoPausa(true);
            frame.agregarLog("=== SIMULACION PAUSADA ===");
        });
    }

    private void onSimulacionReanudada(SimulacionReanudadaEvent event) {
        logService.debug("Evento SimulacionReanudada recibido");
        SwingUtilities.invokeLater(() -> {
            frame.cambiarEstadoPausa(false);
            frame.agregarLog("=== SIMULACION REANUDADA ===");
        });
    }

    private void onSimulacionDetenida(SimulacionDetenidaEvent event) {
        logService.debug("Evento SimulacionDetenida recibido");
        SwingUtilities.invokeLater(() -> {
            frame.habilitarBotonIniciar(true);
            frame.habilitarBotonDetener(false);
            frame.cambiarEstadoPausa(false);
            frame.agregarLog("=== SIMULACION DETENIDA ===");
        });
    }
}