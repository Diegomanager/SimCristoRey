package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;
import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.service.EstadisticasService;

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
