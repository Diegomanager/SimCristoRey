package com.supermercado.application.cooperativa.dto;

import java.util.Set;

public class ServicioDTO {
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

    public ServicioDTO() {}

    public ServicioDTO(String id, String nombre, int duracionMinima, int duracionMaxima,
                       double montoMinimo, double montoMaximo, double tasaInteres,
                       double probabilidad, Set<String> tiposCajaPermitidos, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.duracionMinima = duracionMinima;
        this.duracionMaxima = duracionMaxima;
        this.montoMinimo = montoMinimo;
        this.montoMaximo = montoMaximo;
        this.tasaInteres = tasaInteres;
        this.probabilidad = probabilidad;
        this.tiposCajaPermitidos = tiposCajaPermitidos;
        this.activo = activo;
    }

    // Getters y setters
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

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}