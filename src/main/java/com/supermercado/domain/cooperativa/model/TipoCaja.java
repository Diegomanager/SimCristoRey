package com.supermercado.domain.cooperativa.model;

import java.util.HashSet;
import java.util.Set;

public class TipoCaja {
    private String id;
    private String nombre;
    private String prefijoFicha;
    private double factorVelocidad;
    private int prioridad;
    private Set<String> serviciosEspecializados;
    private boolean activo;

    public TipoCaja(String id, String nombre, String prefijoFicha) {
        this.id = id;
        this.nombre = nombre;
        this.prefijoFicha = prefijoFicha;
        this.factorVelocidad = 1.0;
        this.prioridad = 2;
        this.serviciosEspecializados = new HashSet<>();
        this.activo = true;
    }

    // Constructor sin argumentos (para compatibilidad)
    public TipoCaja() {
        this("GENERAL", "General", "GEN");
    }

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
    public void addServicioEspecializado(String servicioId) { this.serviciosEspecializados.add(servicioId); }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}