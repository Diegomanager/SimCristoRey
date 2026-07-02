package com.supermercado.application.cooperativa.dto;

public class SocioDTO {
    private String id;
    private String ficha;
    private String servicioId;
    private String servicioNombre;
    private double monto;
    private int duracionEstimada;
    private boolean esPreferente;
    private int prioridad;
    private boolean atendida;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }

    public String getServicioId() { return servicioId; }
    public void setServicioId(String servicioId) { this.servicioId = servicioId; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public int getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(int duracionEstimada) { this.duracionEstimada = duracionEstimada; }

    public boolean isEsPreferente() { return esPreferente; }
    public void setEsPreferente(boolean esPreferente) { this.esPreferente = esPreferente; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public boolean isAtendida() { return atendida; }
    public void setAtendida(boolean atendida) { this.atendida = atendida; }
}