package com.supermercado.domain.event;

import com.supermercado.application.dto.EstadisticasDTO;

public class EstadisticasActualizadasEvent {
    private final EstadisticasDTO estadisticas;

    public EstadisticasActualizadasEvent(EstadisticasDTO estadisticas) {
        this.estadisticas = estadisticas;
    }

    public EstadisticasDTO getEstadisticas() {
        return estadisticas;
    }
}