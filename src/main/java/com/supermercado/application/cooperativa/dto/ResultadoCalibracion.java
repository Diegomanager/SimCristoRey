package com.supermercado.application.cooperativa.dto;

import com.supermercado.domain.cooperativa.model.ServicioFinanciero;

import java.util.List;
import java.util.Map;

/**
 * Resultado de calibrar la configuracion a partir de un historial real.
 * Contiene los servicios calibrados (duracion, montos por defecto) y las
 * probabilidades calculadas por codigo de ticket (frecuencia relativa).
 */
public class ResultadoCalibracion {

    private final List<ServicioFinanciero> servicios;
    private final Map<String, Double> probabilidadesPorCodigo;
    private final int totalFichasProcesadas;
    private final int filasIgnoradas;

    public ResultadoCalibracion(List<ServicioFinanciero> servicios,
                                 Map<String, Double> probabilidadesPorCodigo,
                                 int totalFichasProcesadas,
                                 int filasIgnoradas) {
        this.servicios = servicios;
        this.probabilidadesPorCodigo = probabilidadesPorCodigo;
        this.totalFichasProcesadas = totalFichasProcesadas;
        this.filasIgnoradas = filasIgnoradas;
    }

    public List<ServicioFinanciero> getServicios() { return servicios; }
    public Map<String, Double> getProbabilidadesPorCodigo() { return probabilidadesPorCodigo; }
    public int getTotalFichasProcesadas() { return totalFichasProcesadas; }
    public int getFilasIgnoradas() { return filasIgnoradas; }
}