package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Configuracion;
import com.supermercado.domain.supermercado.model.Recomendacion;
import com.supermercado.domain.supermercado.service.DiagnosticoService;
import com.supermercado.domain.supermercado.service.OptimizadorService;
import com.supermercado.application.supermercado.dto.RecomendacionDTO;
import java.util.List;

public class RecomendarMejoraUseCase {
    
    private final DiagnosticoService diagnosticoService;
    private final OptimizadorService optimizadorService;
    
    public RecomendarMejoraUseCase() {
        this.diagnosticoService = new DiagnosticoService();
        this.optimizadorService = new OptimizadorService();
    }
    
    public RecomendacionDTO ejecutar(List<Caja> cajas, Configuracion configuracion) {
        // 1. Diagnosticar
        List<DiagnosticoService.Diagnostico> diagnosticos = 
            diagnosticoService.diagnosticar(cajas);
        
        // 2. Generar recomendación general
        Recomendacion recomendacionGeneral = 
            diagnosticoService.generarRecomendacionGeneral(diagnosticos);
        
        // 3. Contar críticos
        long criticos = diagnosticos.stream()
            .filter(d -> d.getNivel() == DiagnosticoService.NivelAlerta.CRITICO)
            .count();
        
        // 4. Si hay críticos, optimizar
        if (criticos > 0) {
            List<OptimizadorService.ResultadoOptimizacion> resultados = 
                optimizadorService.optimizar(configuracion, 10, 5);
            
            if (!resultados.isEmpty()) {
                OptimizadorService.ResultadoOptimizacion mejor = resultados.get(0);
                
                String mensaje = recomendacionGeneral.getMensaje() + "\n\n" +
                    "CONFIGURACION RECOMENDADA:\n" +
                    "  Normales: " + mejor.getNumCajasNormales() + "\n" +
                    "  Rapidas: " + mejor.getNumCajasRapidas() + "\n" +
                    "  Mejora estimada: " + String.format("%.1f", mejor.getMejoraPorcentaje()) + "%";
                
                return new RecomendacionDTO(
                    mensaje,
                    "CRITICO",
                    mejor.getNumCajasNormales() + " normales + " + 
                    mejor.getNumCajasRapidas() + " rapidas",
                    mejor.getMejoraPorcentaje(),
                    false
                );
            }
        }
        
        // 5. Sin críticos, devolver la recomendación general
        return new RecomendacionDTO(
            recomendacionGeneral.getMensaje(),
            recomendacionGeneral.getPrioridad(),
            recomendacionGeneral.getConfiguracionSugerida(),
            recomendacionGeneral.getMejoraEstimada(),
            false
        );
    }
}