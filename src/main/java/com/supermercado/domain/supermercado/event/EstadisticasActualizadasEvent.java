package com.supermercado.domain.supermercado.event;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;

public class EstadisticasActualizadasEvent {
    private final EstadisticasDTO estadisticas;

    public EstadisticasActualizadasEvent(EstadisticasDTO estadisticas) {
        this.estadisticas = estadisticas;
    }

    public EstadisticasDTO getEstadisticas() {
        return estadisticas;
    }
}