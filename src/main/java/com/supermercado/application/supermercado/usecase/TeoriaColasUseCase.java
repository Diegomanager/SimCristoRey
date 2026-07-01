package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Configuracion;
import com.supermercado.domain.supermercado.model.Recomendacion;
import com.supermercado.domain.supermercado.service.TeoriaColasService;

public class TeoriaColasUseCase {
    
    private final TeoriaColasService teoriaColasService;
    
    public TeoriaColasUseCase() {
        this.teoriaColasService = new TeoriaColasService();
    }
    
    public String ejecutar(Configuracion configuracion) {
        double lambda = configuracion.getClientesPorHora();
        double mu = 60.0 / configuracion.getTiempoAtencionPromedio();
        int c = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
        
        TeoriaColasService.MetricasColas metricas = 
            teoriaColasService.calcularMetricas(lambda, mu, c);
        
        return metricas.toString();
    }
    
    public Recomendacion obtenerRecomendacion(Configuracion configuracion) {
        double lambda = configuracion.getClientesPorHora();
        double mu = 60.0 / configuracion.getTiempoAtencionPromedio();
        int c = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
        
        TeoriaColasService.MetricasColas metricas = 
            teoriaColasService.calcularMetricas(lambda, mu, c);
        
        return teoriaColasService.generarRecomendacionTeoriaColas(metricas);
    }
}