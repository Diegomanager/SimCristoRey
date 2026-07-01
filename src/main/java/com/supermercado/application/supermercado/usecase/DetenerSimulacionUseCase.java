package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.service.SimulacionEngine;

public class DetenerSimulacionUseCase {

    private final SimulacionEngine engine;

    public DetenerSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.detener();
    }
}