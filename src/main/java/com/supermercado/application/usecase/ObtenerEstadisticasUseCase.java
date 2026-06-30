package com.supermercado.application.usecase;

import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.service.EstadisticasService;

import java.util.List;

public class ObtenerEstadisticasUseCase {
    
    private final EstadisticasService estadisticasService;
    
    public ObtenerEstadisticasUseCase() {
        this.estadisticasService = new EstadisticasService();
    }
    
    public EstadisticasDTO ejecutar(List<Caja> cajas) {
        if (cajas == null || cajas.isEmpty()) {
            return new EstadisticasDTO();
        }
        return estadisticasService.calcularEstadisticas(cajas);
    }
}
