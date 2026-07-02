package com.supermercado.application.cooperativa.dto;

import java.util.Set;

public class TipoCajaDTO {
    private String id;
    private String nombre;
    private String prefijoFicha;
    private double factorVelocidad;
    private int prioridad;
    private Set<String> serviciosEspecializados;
    private boolean activo;

    public TipoCajaDTO() {}

    public TipoCajaDTO(String id, String nombre, String prefijoFicha, double factorVelocidad,
                       int prioridad, Set<String> serviciosEspecializados, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.prefijoFicha = prefijoFicha;
        this.factorVelocidad = factorVelocidad;
        this.prioridad = prioridad;
        this.serviciosEspecializados = serviciosEspecializados;
        this.activo = activo;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPrefijoFicha() { return prefijoFicha; }
    public void setPrefijoFicha(String prefijoFicha) { this.prefijoFicha = prefijoFicha; }

    public double getFactorVelocidad() { return factorVelocidad; }
    public void setFactorVelocidad(double factorVelocidad) { this.factorVelocidad = factorVelocidad; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public Set<String> getServiciosEspecializados() { return serviciosEspecializados; }
    public void setServiciosEspecializados(Set<String> serviciosEspecializados) { this.serviciosEspecializados = serviciosEspecializados; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}