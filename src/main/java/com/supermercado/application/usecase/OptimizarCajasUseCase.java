package com.supermercado.application.usecase;

import com.supermercado.domain.model.Configuracion;
import com.supermercado.domain.service.OptimizadorService;
import com.supermercado.application.dto.OptimizacionDTO;
import java.util.List;
import java.util.stream.Collectors;

public class OptimizarCajasUseCase {

    private final OptimizadorService optimizadorService;

    public OptimizarCajasUseCase() {
        this.optimizadorService = new OptimizadorService();
    }

    public OptimizacionDTO ejecutar(Configuracion configuracionBase,
                                    int maxNormales,
                                    int maxRapidas) {
        if (maxNormales <= 0 || maxRapidas <= 0) {
            throw new IllegalArgumentException("Los máximos deben ser positivos");
        }

        List<OptimizadorService.ResultadoOptimizacion> resultados =
            optimizadorService.optimizar(configuracionBase, maxNormales, maxRapidas);

        List<OptimizacionDTO.ResultadoOptimizacion> items = resultados.stream()
            .map(r -> new OptimizacionDTO.ResultadoOptimizacion(
                r.getNumCajasNormales(),
                r.getNumCajasRapidas(),
                r.getTotalCajas(),
                r.getClientesAtendidos(),
                r.getColaMaxima(),
                r.getTiempoPromedio(),
                r.getFactorUtilizacion(),
                r.getMejoraPorcentaje(),
                r.getRecomendacion()
            ))
            .collect(Collectors.toList());

        String mejorConfig = "";
        double mejora = 0.0;
        String recomendacion = "";

        if (!resultados.isEmpty()) {
            OptimizadorService.ResultadoOptimizacion mejor = resultados.get(0);
            mejorConfig = mejor.getNumCajasNormales() + " normales + " +
                         mejor.getNumCajasRapidas() + " rapidas";
            mejora = mejor.getMejoraPorcentaje();
            recomendacion = mejor.getRecomendacion();
        }

        return new OptimizacionDTO(items, mejorConfig, mejora, recomendacion);
    }
}