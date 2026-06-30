package com.supermercado.domain.model;

public class Recomendacion {
    private final String mensaje;
    private final String prioridad;
    private final String configuracionSugerida;
    private final double mejoraEstimada;
    private final boolean aceptada;

    public Recomendacion(String mensaje, String prioridad) {
        this(mensaje, prioridad, null, 0.0, false);
    }

    public Recomendacion(String mensaje, String prioridad,
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

    public Recomendacion conAceptada(boolean aceptada) {
        return new Recomendacion(mensaje, prioridad,
            configuracionSugerida, mejoraEstimada, aceptada);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", prioridad, mensaje);
    }
}