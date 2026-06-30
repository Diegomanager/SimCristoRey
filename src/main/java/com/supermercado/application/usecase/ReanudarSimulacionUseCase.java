package com.supermercado.application.usecase;

import com.supermercado.domain.service.SimulacionEngine;

public class ReanudarSimulacionUseCase {

    private final SimulacionEngine engine;

    public ReanudarSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.reanudar();
    }
}