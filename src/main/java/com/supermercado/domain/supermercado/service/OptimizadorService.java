package com.supermercado.domain.supermercado.service;

import com.supermercado.domain.supermercado.model.Configuracion;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio: Optimizador de configuracion de cajas.
 *
 * Responsabilidad unica: calcular configuraciones optimas de cajas
 * aplicando modelos de teoria de colas para minimizar tiempos de espera.
 *
 * No depende de infraestructura ni de la capa de presentacion.
 */
public class OptimizadorService {

    /**
     * Resultado de evaluar una configuracion especifica de cajas.
     * Todos los campos son privados con acceso via getters (encapsulamiento).
     */
    public static class ResultadoOptimizacion {

        private final int    numCajasNormales;
        private final int    numCajasRapidas;
        private final int    totalCajas;
        private final int    clientesAtendidos;
        private final int    colaMaxima;
        private final double tiempoPromedio;
        private final double factorUtilizacion;
        private final double mejoraPorcentaje;
        private final String recomendacion;

        // Constructor interno - solo OptimizadorService puede crear instancias
        private ResultadoOptimizacion(
                int numCajasNormales, int numCajasRapidas, int totalCajas,
                int clientesAtendidos, int colaMaxima,
                double tiempoPromedio, double factorUtilizacion,
                double mejoraPorcentaje, String recomendacion) {
            this.numCajasNormales  = numCajasNormales;
            this.numCajasRapidas   = numCajasRapidas;
            this.totalCajas        = totalCajas;
            this.clientesAtendidos = clientesAtendidos;
            this.colaMaxima        = colaMaxima;
            this.tiempoPromedio    = tiempoPromedio;
            this.factorUtilizacion = factorUtilizacion;
            this.mejoraPorcentaje  = mejoraPorcentaje;
            this.recomendacion     = recomendacion;
        }

        public int    getNumCajasNormales()  { return numCajasNormales; }
        public int    getNumCajasRapidas()   { return numCajasRapidas; }
        public int    getTotalCajas()        { return totalCajas; }
        public int    getClientesAtendidos() { return clientesAtendidos; }
        public int    getColaMaxima()        { return colaMaxima; }
        public double getTiempoPromedio()    { return tiempoPromedio; }
        public double getFactorUtilizacion() { return factorUtilizacion; }
        public double getMejoraPorcentaje()  { return mejoraPorcentaje; }
        public String getRecomendacion()     { return recomendacion; }
    }

    /**
     * Calcula y ordena las configuraciones optimas de cajas.
     *
     * @param configuracionBase configuracion actual como referencia
     * @param maxNormales       maximo de cajas normales a evaluar
     * @param maxRapidas        maximo de cajas rapidas a evaluar
     * @return lista de resultados ordenada de mejor a peor
     */
    public List<ResultadoOptimizacion> optimizar(
            Configuracion configuracionBase,
            int maxNormales,
            int maxRapidas) {

        List<ResultadoOptimizacion> resultados = new ArrayList<>();

        for (int normales = 4; normales <= maxNormales; normales++) {
            for (int rapidas = 1; rapidas <= maxRapidas; rapidas++) {
                if (normales + rapidas > 12) continue;

                ResultadoOptimizacion resultado = calcularResultado(normales, rapidas);
                resultados.add(resultado);
            }
        }

        resultados.sort((a, b) ->
            Integer.compare(b.getClientesAtendidos(), a.getClientesAtendidos()));

        normalizarMejoras(resultados);
        return resultados;
    }

    private ResultadoOptimizacion calcularResultado(int normales, int rapidas) {
        double lambda     = 60.0;
        double mu         = 60.0 / 5.0;
        double capacidad  = (normales * mu) + (rapidas * mu * 1.5);
        double factor     = lambda / capacidad;

        int    clientes   = (int)(lambda * 10 * Math.min(1.0, 1.0 / factor));
        int    colaMax    = Math.max(5, (int)(factor * 60));
        double tiempoProm = 5.0 / Math.min(1.0, 1.0 / factor);

        String recomendacion;
        if (factor > 0.9) {
            recomendacion = "Sobrecargado - Considere aumentar cajas";
        } else if (factor < 0.5) {
            recomendacion = "Subutilizado - Considere reducir cajas";
        } else {
            recomendacion = "Balanceado - Configuracion optima";
        }

        return new ResultadoOptimizacion(
            normales, rapidas, normales + rapidas,
            clientes, colaMax, tiempoProm, factor,
            0.0, recomendacion
        );
    }

    private void normalizarMejoras(List<ResultadoOptimizacion> resultados) {
        if (resultados.isEmpty()) return;
        int maxClientes = resultados.get(0).getClientesAtendidos();
        // Recrear con mejora calculada
        for (int i = 0; i < resultados.size(); i++) {
            ResultadoOptimizacion r = resultados.get(i);
            double mejora = maxClientes > 0
                ? ((double)(r.getClientesAtendidos() - maxClientes) / maxClientes) * 100
                : 0.0;
            resultados.set(i, new ResultadoOptimizacion(
                r.getNumCajasNormales(), r.getNumCajasRapidas(), r.getTotalCajas(),
                r.getClientesAtendidos(), r.getColaMaxima(),
                r.getTiempoPromedio(), r.getFactorUtilizacion(),
                mejora, r.getRecomendacion()
            ));
        }
    }
}