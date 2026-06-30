package com.supermercado.application.dto;

import java.util.List;

public class OptimizacionDTO {
    private final List<ResultadoOptimizacion> resultados;
    private final String mejorConfiguracion;
    private final double mejoraPorcentaje;
    private final String recomendacion;
    
    public OptimizacionDTO(List<ResultadoOptimizacion> resultados, 
                           String mejorConfiguracion, 
                           double mejoraPorcentaje,
                           String recomendacion) {
        this.resultados = resultados;
        this.mejorConfiguracion = mejorConfiguracion;
        this.mejoraPorcentaje = mejoraPorcentaje;
        this.recomendacion = recomendacion;
    }
    
    public static class ResultadoOptimizacion {
        private final int normales;
        private final int rapidas;
        private final int totalCajas;
        private final int clientesAtendidos;
        private final int colaMaxima;
        private final double tiempoPromedio;
        private final double factorUtilizacion;
        private final double mejora;
        private final String estado;
        
        public ResultadoOptimizacion(int normales, int rapidas, int totalCajas,
                                     int clientesAtendidos, int colaMaxima,
                                     double tiempoPromedio, double factorUtilizacion,
                                     double mejora, String estado) {
            this.normales = normales;
            this.rapidas = rapidas;
            this.totalCajas = totalCajas;
            this.clientesAtendidos = clientesAtendidos;
            this.colaMaxima = colaMaxima;
            this.tiempoPromedio = tiempoPromedio;
            this.factorUtilizacion = factorUtilizacion;
            this.mejora = mejora;
            this.estado = estado;
        }
        
        public int getNormales() { return normales; }
        public int getRapidas() { return rapidas; }
        public int getTotalCajas() { return totalCajas; }
        public int getClientesAtendidos() { return clientesAtendidos; }
        public int getColaMaxima() { return colaMaxima; }
        public double getTiempoPromedio() { return tiempoPromedio; }
        public double getFactorUtilizacion() { return factorUtilizacion; }
        public double getMejora() { return mejora; }
        public String getEstado() { return estado; }
    }
    
    public List<ResultadoOptimizacion> getResultados() { return resultados; }
    public String getMejorConfiguracion() { return mejorConfiguracion; }
    public double getMejoraPorcentaje() { return mejoraPorcentaje; }
    public String getRecomendacion() { return recomendacion; }
}