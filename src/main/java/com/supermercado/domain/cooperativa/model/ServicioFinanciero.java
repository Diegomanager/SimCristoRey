package com.supermercado.domain.cooperativa.model;

import java.util.HashSet;
import java.util.Set;

public class ServicioFinanciero {
    private String id;
    private String nombre;
    private int duracionMinima;
    private int duracionMaxima;
    private double montoMinimo;
    private double montoMaximo;
    private double tasaInteres;
    private double probabilidad;
    private Set<String> tiposCajaPermitidos;
    private boolean activo;
    private String tipoCajaRequerido; // NUEVO

    public ServicioFinanciero(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.tiposCajaPermitidos = new HashSet<>();
        this.activo = true;
        this.probabilidad = 0.0;
        this.tipoCajaRequerido = "GENERAL";
    }

    // Constructor sin argumentos (para compatibilidad)
    public ServicioFinanciero() {
        this("SVC-DEF", "Servicio por defecto");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getDuracionMinima() { return duracionMinima; }
    public void setDuracionMinima(int duracionMinima) { this.duracionMinima = duracionMinima; }

    public int getDuracionMaxima() { return duracionMaxima; }
    public void setDuracionMaxima(int duracionMaxima) { this.duracionMaxima = duracionMaxima; }

    public double getMontoMinimo() { return montoMinimo; }
    public void setMontoMinimo(double montoMinimo) { this.montoMinimo = montoMinimo; }

    public double getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(double montoMaximo) { this.montoMaximo = montoMaximo; }

    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) { this.tasaInteres = tasaInteres; }

    public double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(double probabilidad) { this.probabilidad = probabilidad; }

    public Set<String> getTiposCajaPermitidos() { return tiposCajaPermitidos; }
    public void setTiposCajaPermitidos(Set<String> tiposCajaPermitidos) { this.tiposCajaPermitidos = tiposCajaPermitidos; }
    public void addTipoCajaPermitido(String tipoCajaId) { this.tiposCajaPermitidos.add(tipoCajaId); }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // NUEVO: tipoCajaRequerido
    public String getTipoCajaRequerido() { return tipoCajaRequerido; }
    public void setTipoCajaRequerido(String tipoCajaRequerido) { this.tipoCajaRequerido = tipoCajaRequerido; }
}