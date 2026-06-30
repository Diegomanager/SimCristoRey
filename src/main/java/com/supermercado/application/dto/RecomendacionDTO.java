package com.supermercado.application.dto;

public class RecomendacionDTO {
    private final String mensaje;
    private final String prioridad;
    private final String configuracionSugerida;
    private final double mejoraEstimada;
    private final boolean aceptada;
    
    public RecomendacionDTO(String mensaje, String prioridad, 
                            String configuracionSugerida, 
                            double mejoraEstimada, 
                            boolean aceptada) {
        this.mensaje = mensaje;
        this.prioridad = prioridad;
        this.configuracionSugerida = configuracionSugerida;
        this.mejoraEstimada = mejoraEstimada;
        this.aceptada = aceptada;
    }
    
    public String getMensaje() { return mensaje; }
    public String getPrioridad() { return prioridad; }
    public String getConfiguracionSugerida() { return configuracionSugerida; }
    public double getMejoraEstimada() { return mejoraEstimada; }
    public boolean isAceptada() { return aceptada; }
    
    public RecomendacionDTO conAceptada(boolean aceptada) {
        return new RecomendacionDTO(mensaje, prioridad, 
            configuracionSugerida, mejoraEstimada, aceptada);
    }
}