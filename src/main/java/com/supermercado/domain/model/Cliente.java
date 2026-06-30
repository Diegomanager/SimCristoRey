package com.supermercado.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio: Cliente.
 * Representa a un cliente que llega al supermercado.
 *
 * Reglas de negocio:
 * - Un cliente es "rápido" si tiene 10 o menos artículos (valor por defecto).
 * - El límite puede ser sobrescrito externamente mediante setRapido().
 * - El tiempo de atención es asignado externamente.
 */
public class Cliente {

    private final String id;
    private final int cantidadArticulos;
    private boolean rapido;  // mutable para permitir límite configurable
    private long tiempoLlegada;
    private long tiempoInicioAtencion;
    private long tiempoSalida;
    private int tiempoAtencionReal;

    // ============================================================
    // Constructores
    // ============================================================

    public Cliente(String id, int cantidadArticulos) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del cliente no puede estar vacío");
        }
        if (cantidadArticulos < 0) {
            throw new IllegalArgumentException("La cantidad de artículos no puede ser negativa");
        }
        this.id = id;
        this.cantidadArticulos = cantidadArticulos;
        this.rapido = cantidadArticulos <= 10;  // valor por defecto
        this.tiempoLlegada = 0;
        this.tiempoInicioAtencion = 0;
        this.tiempoSalida = 0;
        this.tiempoAtencionReal = 0;
    }

    public Cliente(int id, int cantidadArticulos) {
        this("Cliente-" + id, cantidadArticulos);
    }

    // ============================================================
    // Getters (inmutables excepto rapido que puede cambiar)
    // ============================================================

    public String getId() { return id; }
    public int getCantidadArticulos() { return cantidadArticulos; }
    public boolean esRapido() { return rapido; }

    public long getTiempoLlegada() { return tiempoLlegada; }
    public long getTiempoInicioAtencion() { return tiempoInicioAtencion; }
    public long getTiempoSalida() { return tiempoSalida; }
    public int getTiempoAtencionReal() { return tiempoAtencionReal; }

    // ============================================================
    // Setters (incluyendo setRapido para permitir configuración externa)
    // ============================================================

    public void setRapido(boolean rapido) {
        this.rapido = rapido;
    }

    public void setTiempoLlegada(long tiempoLlegada) {
        if (tiempoLlegada < 0) {
            throw new IllegalArgumentException("El tiempo de llegada no puede ser negativo");
        }
        this.tiempoLlegada = tiempoLlegada;
    }

    public void setTiempoInicioAtencion(long tiempoInicioAtencion) {
        if (tiempoInicioAtencion < 0) {
            throw new IllegalArgumentException("El tiempo de inicio de atención no puede ser negativo");
        }
        this.tiempoInicioAtencion = tiempoInicioAtencion;
    }

    public void setTiempoSalida(long tiempoSalida) {
        if (tiempoSalida < 0) {
            throw new IllegalArgumentException("El tiempo de salida no puede ser negativo");
        }
        this.tiempoSalida = tiempoSalida;
    }

    public void setTiempoAtencionReal(int tiempoAtencionReal) {
        if (tiempoAtencionReal < 0) {
            throw new IllegalArgumentException("El tiempo de atención real no puede ser negativo");
        }
        this.tiempoAtencionReal = tiempoAtencionReal;
    }

    // ============================================================
    // Métodos de negocio
    // ============================================================

    /**
     * Calcula el tiempo de espera del cliente en minutos simulados.
     * El tiempo de espera es desde que llega hasta que empieza a ser atendido.
     */
    public long calcularTiempoEspera() {
        if (tiempoLlegada == 0 || tiempoInicioAtencion == 0) {
            return 0;
        }
        return tiempoInicioAtencion - tiempoLlegada;
    }

    /**
     * Calcula el tiempo total en el sistema (espera + atención).
     */
    public long calcularTiempoTotal() {
        if (tiempoLlegada == 0 || tiempoSalida == 0) {
            return 0;
        }
        return tiempoSalida - tiempoLlegada;
    }

    // ============================================================
    // Object overrides
    // ============================================================

    @Override
    public String toString() {
        return String.format("%s [%s, %d artículos]",
            id, rapido ? "RÁPIDO" : "NORMAL", cantidadArticulos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}