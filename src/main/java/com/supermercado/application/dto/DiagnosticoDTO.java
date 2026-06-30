package com.supermercado.application.dto;

import java.util.List;

public class DiagnosticoDTO {
    private final List<DiagnosticoItem> items;
    private final long criticos;
    private final long alertas;
    private final long atencion;
    private final String resumenEjecutivo;
    
    public DiagnosticoDTO(List<DiagnosticoItem> items, long criticos, 
                          long alertas, long atencion) {
        this(items, criticos, alertas, atencion, null);
    }
    
    public DiagnosticoDTO(List<DiagnosticoItem> items, long criticos, 
                          long alertas, long atencion, String resumenEjecutivo) {
        this.items = items;
        this.criticos = criticos;
        this.alertas = alertas;
        this.atencion = atencion;
        this.resumenEjecutivo = resumenEjecutivo;
    }
    
    public static class DiagnosticoItem {
        private final String cajaId;
        private final String nivel;
        private final String icono;
        private final String mensaje;
        private final String sugerencia;
        private final int colaActual;
        private final int colaMaxima;
        
        public DiagnosticoItem(String cajaId, String nivel, String icono,
                               String mensaje, String sugerencia,
                               int colaActual, int colaMaxima) {
            this.cajaId = cajaId;
            this.nivel = nivel;
            this.icono = icono;
            this.mensaje = mensaje;
            this.sugerencia = sugerencia;
            this.colaActual = colaActual;
            this.colaMaxima = colaMaxima;
        }
        
        public String getCajaId() { return cajaId; }
        public String getNivel() { return nivel; }
        public String getIcono() { return icono; }
        public String getMensaje() { return mensaje; }
        public String getSugerencia() { return sugerencia; }
        public int getColaActual() { return colaActual; }
        public int getColaMaxima() { return colaMaxima; }
    }
    
    public List<DiagnosticoItem> getItems() { return items; }
    public long getCriticos() { return criticos; }
    public long getAlertas() { return alertas; }
    public long getAtencion() { return atencion; }
    public String getResumenEjecutivo() { return resumenEjecutivo; }
    
    public boolean tieneProblemas() {
        return criticos > 0 || alertas > 0;
    }
    
    public String getEstadoGeneral() {
        if (criticos > 0) return "CRITICO";
        if (alertas > 0) return "ALERTA";
        if (atencion > 0) return "ATENCION";
        return "NORMAL";
    }
}