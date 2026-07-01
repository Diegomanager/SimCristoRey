package com.supermercado.domain.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa un servicio financiero ofrecido por la cooperativa.
 * Ejemplos: Préstamo Personal, Depósito a Plazo Fijo, Pago de Servicios, etc.
 */
public class ServicioFinanciero {
    private final String id;
    private final String nombre;
    private int duracionMinima;
    private int duracionMaxima;
    private double montoMinimo;
    private double montoMaximo;
    private double tasaInteres;
    private double probabilidad;
    private Set<String> tiposCajaPermitidos;
    private boolean activo;

    public ServicioFinanciero(String id, String nombre) {
        this.id = Objects.requireNonNull(id, "ID no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        this.tiposCajaPermitidos = new HashSet<>();
        this.activo = true;
        this.duracionMinima = 1;
        this.duracionMaxima = 10;
        this.montoMinimo = 10.0;
        this.montoMaximo = 1000.0;
        this.tasaInteres = 5.0;
        this.probabilidad = 0.0;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDuracionMinima() { return duracionMinima; }
    public void setDuracionMinima(int duracionMinima) {
        if (duracionMinima < 0) throw new IllegalArgumentException("Duración mínima no puede ser negativa");
        if (duracionMaxima < duracionMinima) throw new IllegalArgumentException("Duración mínima no puede ser mayor que máxima");
        this.duracionMinima = duracionMinima;
    }
    public int getDuracionMaxima() { return duracionMaxima; }
    public void setDuracionMaxima(int duracionMaxima) {
        if (duracionMaxima < 0) throw new IllegalArgumentException("Duración máxima no puede ser negativa");
        if (duracionMaxima < duracionMinima) throw new IllegalArgumentException("Duración máxima no puede ser menor que mínima");
        this.duracionMaxima = duracionMaxima;
    }
    public double getMontoMinimo() { return montoMinimo; }
    public void setMontoMinimo(double montoMinimo) {
        if (montoMinimo < 0) throw new IllegalArgumentException("Monto mínimo no puede ser negativo");
        if (montoMaximo < montoMinimo) throw new IllegalArgumentException("Monto mínimo no puede ser mayor que máximo");
        this.montoMinimo = montoMinimo;
    }
    public double getMontoMaximo() { return montoMaximo; }
    public void setMontoMaximo(double montoMaximo) {
        if (montoMaximo < 0) throw new IllegalArgumentException("Monto máximo no puede ser negativo");
        if (montoMaximo < montoMinimo) throw new IllegalArgumentException("Monto máximo no puede ser menor que mínimo");
        this.montoMaximo = montoMaximo;
    }
    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) {
        if (tasaInteres < 0) throw new IllegalArgumentException("Tasa de interés no puede ser negativa");
        this.tasaInteres = tasaInteres;
    }
    public double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(double probabilidad) {
        if (probabilidad < 0 || probabilidad > 100) {
            throw new IllegalArgumentException("La probabilidad debe estar entre 0 y 100");
        }
        this.probabilidad = probabilidad;
    }
    public Set<String> getTiposCajaPermitidos() { return Set.copyOf(tiposCajaPermitidos); }
    public void agregarTipoCajaPermitido(String tipoCajaId) {
        tiposCajaPermitidos.add(tipoCajaId);
    }
    public void quitarTipoCajaPermitido(String tipoCajaId) {
        tiposCajaPermitidos.remove(tipoCajaId);
    }
    public boolean puedeSerAtendidoPor(String tipoCajaId) {
        return tiposCajaPermitidos.isEmpty() || tiposCajaPermitidos.contains(tipoCajaId);
    }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public double generarMontoAleatorio() {
        return montoMinimo + Math.random() * (montoMaximo - montoMinimo);
    }
    public int generarDuracionAleatoria() {
        return duracionMinima + (int)(Math.random() * (duracionMaxima - duracionMinima + 1));
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (%.1f%%)", nombre, id, probabilidad);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicioFinanciero that = (ServicioFinanciero) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}
