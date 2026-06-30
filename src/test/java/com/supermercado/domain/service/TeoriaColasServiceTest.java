package com.supermercado.domain.service;

import com.supermercado.domain.model.Recomendacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeoriaColasServiceTest {
    
    private TeoriaColasService service;
    
    @BeforeEach
    void setUp() {
        service = new TeoriaColasService();
    }
    
    @Test
    void testCalcularMetricas_SistemaEstable() {
        double lambda = 60.0;
        double mu = 15.0;
        int c = 5;
        
        TeoriaColasService.MetricasColas metricas = 
            service.calcularMetricas(lambda, mu, c);
        
        assertTrue(metricas.rho < 1);
        assertTrue(metricas.L >= 0);
        assertTrue(metricas.W >= 0);
    }
    
    @Test
    void testCalcularMetricas_SistemaSaturado() {
        double lambda = 100.0;
        double mu = 10.0;
        int c = 5;
        
        TeoriaColasService.MetricasColas metricas = 
            service.calcularMetricas(lambda, mu, c);
        
        assertTrue(metricas.rho >= 1);
        assertEquals(Double.POSITIVE_INFINITY, metricas.L);
        assertEquals(Double.POSITIVE_INFINITY, metricas.W);
    }
    
    @Test
    void testGenerarRecomendacion_Saturado() {
        double lambda = 100.0;
        double mu = 10.0;
        int c = 5;
        
        TeoriaColasService.MetricasColas metricas = 
            service.calcularMetricas(lambda, mu, c);
        
        Recomendacion recomendacion = 
            service.generarRecomendacionTeoriaColas(metricas);
        
        assertEquals("CRITICO", recomendacion.getPrioridad());
        assertTrue(recomendacion.getMensaje().contains("SATURADO"));
    }
    
    @Test
    void testGenerarRecomendacion_Optimo() {
        double lambda = 60.0;
        double mu = 20.0;
        int c = 4;
        
        TeoriaColasService.MetricasColas metricas = 
            service.calcularMetricas(lambda, mu, c);
        
        Recomendacion recomendacion = 
            service.generarRecomendacionTeoriaColas(metricas);
        
        assertEquals("NORMAL", recomendacion.getPrioridad());
        assertTrue(recomendacion.getMensaje().contains("OPTIMO"));
    }
}