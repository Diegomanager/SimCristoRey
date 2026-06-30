package com.supermercado.application.usecase;

import com.supermercado.domain.service.SimulacionEngine;

public class DetenerSimulacionUseCase {

    private final SimulacionEngine engine;

    public DetenerSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.detener();
    }
}