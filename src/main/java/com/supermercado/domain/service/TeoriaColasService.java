package com.supermercado.domain.service;

import com.supermercado.domain.model.Recomendacion;

public class TeoriaColasService {
    
    public static class MetricasColas {
        public double lambda;
        public double mu;
        public int c;
        public double rho;
        public double L;
        public double Lq;
        public double W;
        public double Wq;
        public double p0;
        
        @Override
        public String toString() {
            String estado = rho >= 1 ? "SATURADO" : "ESTABLE";
            return String.format(
                "METRICAS DE TEORIA DE COLAS (M/M/%d):\n" +
                "  lambda (llegada): %.2f clientes/hora\n" +
                "  mu (servicio): %.2f clientes/hora\n" +
                "  rho (utilizacion): %.2f%% (%s)\n" +
                "  L (en sistema): %.2f clientes\n" +
                "  Lq (en cola): %.2f clientes\n" +
                "  W (en sistema): %.2f min\n" +
                "  Wq (en cola): %.2f min\n" +
                "  P0 (sistema vacio): %.2f%%",
                c, lambda, mu, rho * 100, estado,
                L, Lq, W * 60, Wq * 60, p0 * 100
            );
        }
    }
    
    public MetricasColas calcularMetricas(double lambda, double mu, int c) {
        MetricasColas metrics = new MetricasColas();
        metrics.lambda = lambda;
        metrics.mu = mu;
        metrics.c = c;
        metrics.rho = lambda / (c * mu);
        
        if (metrics.rho >= 1) {
            metrics.L = Double.POSITIVE_INFINITY;
            metrics.Lq = Double.POSITIVE_INFINITY;
            metrics.W = Double.POSITIVE_INFINITY;
            metrics.Wq = Double.POSITIVE_INFINITY;
            metrics.p0 = 0;
            return metrics;
        }
        
        double sum = 0;
        for (int i = 0; i < c; i++) {
            sum += Math.pow(lambda/mu, i) / factorial(i);
        }
        sum += Math.pow(lambda/mu, c) / (factorial(c) * (1 - metrics.rho));
        metrics.p0 = 1 / sum;
        
        metrics.Lq = (metrics.p0 * Math.pow(lambda/mu, c) * metrics.rho) /
                    (factorial(c) * Math.pow(1 - metrics.rho, 2));
        metrics.L = metrics.Lq + metrics.rho;
        metrics.Wq = metrics.Lq / lambda;
        metrics.W = metrics.Wq + (1 / mu);
        
        return metrics;
    }
    
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    public Recomendacion generarRecomendacionTeoriaColas(MetricasColas metrics) {
        String mensaje;
        String prioridad;
        String configSugerida;
        double mejora = 0;
        
        if (metrics.rho >= 1) {
            prioridad = "CRITICO";
            mensaje = String.format(
                "EL SISTEMA ESTA SATURADO!\n" +
                "  Factor de utilizacion: %.1f%% (DEBE ser < 100%%)\n" +
                "  Se necesitan mas cajas urgentemente\n" +
                "  Recomendacion: Aumentar a %d cajas totales",
                metrics.rho * 100,
                (int)(Math.ceil(metrics.lambda / metrics.mu) + 1)
            );
            configSugerida = "Aumentar cajas a " + 
                (int)(Math.ceil(metrics.lambda / metrics.mu) + 1);
            mejora = 30;
        } else if (metrics.rho > 0.8) {
            prioridad = "ALTO";
            mensaje = String.format(
                "SISTEMA CERCA DE SATURACION!\n" +
                "  Factor de utilizacion: %.1f%%\n" +
                "  Cola promedio: %.1f clientes\n" +
                "  Tiempo espera: %.1f min\n" +
                "  Recomendacion: Considerar aumentar cajas",
                metrics.rho * 100,
                metrics.Lq,
                metrics.Wq * 60
            );
            configSugerida = "Considerar agregar 1 caja";
            mejora = 15;
        } else if (metrics.rho < 0.5) {
            prioridad = "MEDIO";
            mensaje = String.format(
                "SISTEMA SUBUTILIZADO\n" +
                "  Factor de utilizacion: %.1f%%\n" +
                "  Cola promedio: %.1f clientes\n" +
                "  Tiempo espera: %.1f min\n" +
                "  Recomendacion: Reducir cajas para optimizar costos",
                metrics.rho * 100,
                metrics.Lq,
                metrics.Wq * 60
            );
            configSugerida = "Reducir cajas en 1 unidad";
            mejora = 5;
        } else {
            prioridad = "NORMAL";
            mensaje = String.format(
                "SISTEMA OPTIMO!\n" +
                "  Factor de utilizacion: %.1f%% (rango ideal)\n" +
                "  Cola promedio: %.1f clientes\n" +
                "  Tiempo espera: %.1f min\n" +
                "  Mantener configuracion actual",
                metrics.rho * 100,
                metrics.Lq,
                metrics.Wq * 60
            );
            configSugerida = "Mantener configuracion actual";
            mejora = 0;
        }
        
        return new Recomendacion(mensaje, prioridad, configSugerida, mejora, false);
    }
}