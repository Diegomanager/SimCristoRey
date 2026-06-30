package com.supermercado;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.port.IConfiguracionRepositorio;
import com.supermercado.application.port.ILogService;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;
import com.supermercado.infrastructure.adapter.ui.SwingEventAdapter;
import com.supermercado.infrastructure.config.AppConfig;
import com.supermercado.presentation.controller.SimulacionController;
import com.supermercado.presentation.view.SimuladorFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Punto de entrada de la aplicacion.
 *
 * Responsabilidad: construir el grafo de dependencias y
 * arrancar la interfaz grafica en el Event Dispatch Thread.
 *
 * Sigue el patron Composition Root: todas las dependencias
 * se construyen aqui y se pasan por constructor.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("=== INICIANDO BOTTLENECK BUSTER ===");
        log.info("Sistema de simulación predictiva para la identificación y resolución de cuellos de botella.");

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            log.warn("No se pudo establecer LookAndFeel", e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // Composition Root: construir todas las dependencias
                AppConfig appConfig = new AppConfig();

                ILogService              logService  = appConfig.getLogService();
                IConfiguracionRepositorio configRepo  = appConfig.getConfiguracionRepositorio();
                EventBusAdapter          eventBus    = appConfig.getEventBus();

                // Vista - recibe dependencias por constructor
                SimuladorFrame frame = new SimuladorFrame(logService, configRepo);

                // Adaptador UI - escucha eventos del dominio y actualiza la vista
                new SwingEventAdapter(frame, eventBus, logService);

                // Controlador - orquesta los casos de uso
                SimulacionController controller = new SimulacionController(
                    frame,
                    logService,
                    appConfig.getIniciarSimulacionUseCase(),
                    appConfig.getPausarSimulacionUseCase(),
                    appConfig.getReanudarSimulacionUseCase(),
                    appConfig.getDetenerSimulacionUseCase()
                );

                frame.setController(controller);
                ConfiguracionDTO config = configRepo.cargar();
                frame.setConfiguracion(config);
                frame.setVisible(true);

                log.info("GUI iniciada correctamente");

            } catch (Exception e) {
                log.error("Error al iniciar la GUI", e);
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicacion: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}