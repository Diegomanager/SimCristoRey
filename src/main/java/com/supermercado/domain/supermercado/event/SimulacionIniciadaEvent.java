package com.supermercado.domain.supermercado.event;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;

/**
 * Evento que se dispara cuando la simulación comienza.
 * Contiene la configuración utilizada para la simulación.
 */
public class SimulacionIniciadaEvent {
    private final ConfiguracionDTO config;

    public SimulacionIniciadaEvent(ConfiguracionDTO config) {
        this.config = config;
    }

    public ConfiguracionDTO getConfig() {
        return config;
    }
}