package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.service.SimulacionEngine;

public class ReanudarSimulacionUseCase {

    private final SimulacionEngine engine;

    public ReanudarSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.reanudar();
    }
}