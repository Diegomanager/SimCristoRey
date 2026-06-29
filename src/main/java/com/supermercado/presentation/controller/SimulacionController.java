package com.supermercado.presentation.controller;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.usecase.*;
import com.supermercado.application.port.ILogService;
import com.supermercado.presentation.view.SimuladorFrame;

public class SimulacionController {

    private final ILogService logService;
    private final IniciarSimulacionUseCase iniciarUseCase;
    private final PausarSimulacionUseCase pausarUseCase;
    private final ReanudarSimulacionUseCase reanudarUseCase;
    private final DetenerSimulacionUseCase detenerUseCase;
    private final SimuladorFrame view;
    private Thread hiloSimulacion;

    public SimulacionController(
            SimuladorFrame view,
            ILogService logService,
            IniciarSimulacionUseCase iniciarUseCase,
            PausarSimulacionUseCase pausarUseCase,
            ReanudarSimulacionUseCase reanudarUseCase,
            DetenerSimulacionUseCase detenerUseCase) {
        this.view = view;
        this.logService = logService;
        this.iniciarUseCase = iniciarUseCase;
        this.pausarUseCase = pausarUseCase;
        this.reanudarUseCase = reanudarUseCase;
        this.detenerUseCase = detenerUseCase;
        logService.info("Controlador de simulacion inicializado (control directo + eventos)");
    }

    public void iniciarSimulacion(ConfiguracionDTO config) {
        if (iniciarUseCase.isEjecutando()) {
            logService.warn("Intento de iniciar simulacion cuando ya esta activa");
            return;
        }
        if (config == null) {
            logService.error("Configuracion nula");
            view.mostrarError("Error", "La configuracion es nula");
            return;
        }
        view.habilitarBotonIniciar(false);
        view.habilitarBotonDetener(true);
        view.limpiarLog();

        hiloSimulacion = new Thread(() -> {
            try {
                iniciarUseCase.ejecutar(config);
                view.habilitarBotonIniciar(true);
                view.habilitarBotonDetener(false);
                view.cambiarEstadoPausa(false);
            } catch (InterruptedException e) {
                view.agregarLog("=== SIMULACION INTERRUMPIDA ===");
                view.habilitarBotonIniciar(true);
                view.habilitarBotonDetener(false);
                view.cambiarEstadoPausa(false);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logService.error("Error en la simulacion", e);
                view.mostrarError("Error", "Error en la simulacion: " + e.getMessage());
                view.habilitarBotonIniciar(true);
                view.habilitarBotonDetener(false);
                view.cambiarEstadoPausa(false);
            }
        });
        hiloSimulacion.start();
    }

    public void detenerSimulacion() {
        if (!iniciarUseCase.isEjecutando()) {
            logService.warn("Intento de detener simulacion cuando no esta activa");
            return;
        }
        logService.info("Deteniendo simulacion");
        detenerUseCase.ejecutar();
        view.habilitarBotonIniciar(true);
        view.habilitarBotonDetener(false);
        view.cambiarEstadoPausa(false);
        view.agregarLog("=== SIMULACION DETENIDA ===");
        if (hiloSimulacion != null && hiloSimulacion.isAlive()) {
            hiloSimulacion.interrupt();
        }
    }

    public void pausarSimulacion() {
        if (!iniciarUseCase.isEjecutando()) {
            logService.warn("Intento de pausar simulacion cuando no esta activa");
            return;
        }
        if (!iniciarUseCase.isPausado()) {
            logService.info("Pausando simulacion");
            pausarUseCase.ejecutar();
            view.cambiarEstadoPausa(true);
            view.agregarLog("=== SIMULACION PAUSADA ===");
        } else {
            logService.info("Reanudando simulacion");
            reanudarUseCase.ejecutar();
            view.cambiarEstadoPausa(false);
            view.agregarLog("=== SIMULACION REANUDADA ===");
        }
    }

    public boolean isSimulacionActiva() {
        return iniciarUseCase.isEjecutando();
    }

    public boolean isPausado() {
        return iniciarUseCase.isPausado();
    }
}