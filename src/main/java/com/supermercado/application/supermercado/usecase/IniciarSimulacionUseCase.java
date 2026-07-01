package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import com.supermercado.application.supermercado.dto.EstadisticasDTO;
import com.supermercado.domain.supermercado.service.SimulacionEngine;

/**
 * Caso de uso para iniciar la simulación.
 * Delega toda la lógica en el motor de simulación (dominio).
 * 
 * @author Bazoalto Andia Carlos Diego
 */
public class IniciarSimulacionUseCase {

    private final SimulacionEngine engine;

    public IniciarSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar(ConfiguracionDTO config) throws InterruptedException {
        engine.iniciar(config);
    }

    public void detener() {
        engine.detener();
    }

    public void pausar() {
        engine.pausar();
    }

    public void reanudar() {
        engine.reanudar();
    }

    public boolean isEjecutando() {
        return engine.isEjecutando();
    }

    public boolean isPausado() {
        return engine.isPausado();
    }

    public EstadisticasDTO obtenerEstadisticas() {
        return engine.obtenerEstadisticas();
    }
}