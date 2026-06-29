package com.supermercado.infrastructure.config;

import com.supermercado.application.port.IConfiguracionRepositorio;
import com.supermercado.application.port.ILogService;
import com.supermercado.application.port.IReporteExportador;
import com.supermercado.application.usecase.*;
import com.supermercado.domain.service.SimulacionEngine;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;
import com.supermercado.infrastructure.repository.ConfiguracionRepositorioImpl;
import com.supermercado.infrastructure.service.LogServiceImpl;
import com.supermercado.infrastructure.service.ReporteTextoImpl;

/**
 * Contenedor de dependencias de la aplicación.
 * Construye todas las dependencias de infraestructura y dominio.
 * No depende de la capa de presentación.
 * 
 * @author Bazoalto Andia Carlos Diego
 */
public class AppConfig {

    private final ILogService logService;
    private final IConfiguracionRepositorio configuracionRepositorio;
    private final IReporteExportador reporteExportador;
    private final EventBusAdapter eventBus;
    private final SimulacionEngine engine;
    private final IniciarSimulacionUseCase iniciarSimulacionUseCase;
    private final PausarSimulacionUseCase pausarSimulacionUseCase;
    private final ReanudarSimulacionUseCase reanudarSimulacionUseCase;
    private final DetenerSimulacionUseCase detenerSimulacionUseCase;

    public AppConfig() {
        this.logService = new LogServiceImpl();
        this.configuracionRepositorio = new ConfiguracionRepositorioImpl(logService);
        this.reporteExportador = new ReporteTextoImpl(logService);
        this.eventBus = new EventBusAdapter();
        this.engine = new SimulacionEngine(logService, eventBus);
        this.iniciarSimulacionUseCase = new IniciarSimulacionUseCase(engine);
        this.pausarSimulacionUseCase = new PausarSimulacionUseCase(engine);
        this.reanudarSimulacionUseCase = new ReanudarSimulacionUseCase(engine);
        this.detenerSimulacionUseCase = new DetenerSimulacionUseCase(engine);
    }

    public ILogService getLogService() {
        return logService;
    }

    public IConfiguracionRepositorio getConfiguracionRepositorio() {
        return configuracionRepositorio;
    }

    public IReporteExportador getReporteExportador() {
        return reporteExportador;
    }

    public EventBusAdapter getEventBus() {
        return eventBus;
    }

    public IniciarSimulacionUseCase getIniciarSimulacionUseCase() {
        return iniciarSimulacionUseCase;
    }

    public PausarSimulacionUseCase getPausarSimulacionUseCase() {
        return pausarSimulacionUseCase;
    }

    public ReanudarSimulacionUseCase getReanudarSimulacionUseCase() {
        return reanudarSimulacionUseCase;
    }

    public DetenerSimulacionUseCase getDetenerSimulacionUseCase() {
        return detenerSimulacionUseCase;
    }
}