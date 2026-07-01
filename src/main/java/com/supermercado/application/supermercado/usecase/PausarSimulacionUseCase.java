package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.service.SimulacionEngine;

public class PausarSimulacionUseCase {

    private final SimulacionEngine engine;

    public PausarSimulacionUseCase(SimulacionEngine engine) {
        this.engine = engine;
    }

    public void ejecutar() {
        engine.pausar();
    }
}