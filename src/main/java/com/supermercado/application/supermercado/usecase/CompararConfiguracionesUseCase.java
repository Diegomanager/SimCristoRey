package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Configuracion;
import com.supermercado.domain.supermercado.service.OptimizadorService;
import com.supermercado.application.supermercado.dto.OptimizacionDTO;
import java.util.List;

public class CompararConfiguracionesUseCase {
    
    private final OptimizadorService optimizadorService;
    
    public CompararConfiguracionesUseCase() {
        this.optimizadorService = new OptimizadorService();
    }
    
    public String ejecutar(Configuracion configActual, 
                           Configuracion configPropuesta) {
        
        StringBuilder comparacion = new StringBuilder();
        comparacion.append("=== COMPARACION DE CONFIGURACIONES ===\n\n");
        
        comparacion.append("CONFIGURACION ACTUAL:\n");
        comparacion.append("  Normales: ").append(configActual.getNumCajasNormales()).append("\n");
        comparacion.append("  Rapidas: ").append(configActual.getNumCajasRapidas()).append("\n");
        comparacion.append("  Total: ").append(configActual.getNumCajasNormales() + configActual.getNumCajasRapidas()).append("\n\n");
        
        comparacion.append("CONFIGURACION PROPUESTA:\n");
        comparacion.append("  Normales: ").append(configPropuesta.getNumCajasNormales()).append("\n");
        comparacion.append("  Rapidas: ").append(configPropuesta.getNumCajasRapidas()).append("\n");
        comparacion.append("  Total: ").append(configPropuesta.getNumCajasNormales() + configPropuesta.getNumCajasRapidas()).append("\n\n");
        
        int diffNormales = configPropuesta.getNumCajasNormales() - configActual.getNumCajasNormales();
        int diffRapidas = configPropuesta.getNumCajasRapidas() - configActual.getNumCajasRapidas();
        int diffTotal = diffNormales + diffRapidas;
        
        comparacion.append("DIFERENCIAS:\n");
        comparacion.append("  Normales: ").append(diffNormales > 0 ? "+" : "").append(diffNormales).append("\n");
        comparacion.append("  Rapidas: ").append(diffRapidas > 0 ? "+" : "").append(diffRapidas).append("\n");
        comparacion.append("  Total: ").append(diffTotal > 0 ? "+" : "").append(diffTotal).append("\n\n");
        
        if (diffTotal > 0) {
            comparacion.append("RECOMENDACION: Aumentar el numero de cajas\n");
        } else if (diffTotal < 0) {
            comparacion.append("RECOMENDACION: Reducir el numero de cajas\n");
        } else {
            comparacion.append("RECOMENDACION: Mantener el mismo numero de cajas\n");
            comparacion.append("  Considerar redistribuir entre normales y rapidas\n");
        }
        
        return comparacion.toString();
    }
}