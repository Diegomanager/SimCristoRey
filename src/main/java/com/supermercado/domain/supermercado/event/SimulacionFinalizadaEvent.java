package com.supermercado.domain.supermercado.event;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;

/**
 * Evento que se dispara cuando la simulación finaliza.
 * Incluye las estadísticas finales.
 */
public class SimulacionFinalizadaEvent {
    private final EstadisticasDTO estadisticas;

    public SimulacionFinalizadaEvent(EstadisticasDTO estadisticas) {
        this.estadisticas = estadisticas;
    }

    public EstadisticasDTO getEstadisticas() {
        return estadisticas;
    }
}