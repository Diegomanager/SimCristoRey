package com.supermercado.application.cooperativa.dto;

public class CajaDTO {
    private String id;
    private String tipoId;
    private String tipoNombre;
    private String estado;
    private SocioDTO socioActual;
    private int totalAtendidos;
    private double montoTotalAtendido;
    private boolean activa;

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipoId() { return tipoId; }
    public void setTipoId(String tipoId) { this.tipoId = tipoId; }

    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public SocioDTO getSocioActual() { return socioActual; }
    public void setSocioActual(SocioDTO socioActual) { this.socioActual = socioActual; }

    public int getTotalAtendidos() { return totalAtendidos; }
    public void setTotalAtendidos(int totalAtendidos) { this.totalAtendidos = totalAtendidos; }

    public double getMontoTotalAtendido() { return montoTotalAtendido; }
    public void setMontoTotalAtendido(double montoTotalAtendido) { this.montoTotalAtendido = montoTotalAtendido; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}