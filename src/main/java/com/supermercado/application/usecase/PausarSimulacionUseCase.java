package com.supermercado.application.usecase;

import com.supermercado.domain.service.SimulacionEngine;

public class PausarSimulacionUseCase {

    private final SimulacionEngine engine;

    public PausarSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.pausar();
    }
}