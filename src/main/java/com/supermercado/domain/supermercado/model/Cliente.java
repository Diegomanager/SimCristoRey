package com.supermercado.domain.supermercado.model;

import java.util.Objects;

public class Cliente {

    private final String id;
    private final int cantidadArticulos;
    private boolean rapido;
    private boolean atendido;          // ✅ NUEVO: indica si ya fue atendido
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
        this.rapido = cantidadArticulos <= 10;
        this.atendido = false;          // ✅ NUEVO
        this.tiempoLlegada = 0;
        this.tiempoInicioAtencion = 0;
        this.tiempoSalida = 0;
        this.tiempoAtencionReal = 0;
    }

    public Cliente(int id, int cantidadArticulos) {
        this("Cliente-" + id, cantidadArticulos);
    }

    // ============================================================
    // Getters y Setters
    // ============================================================

    public String getId() { return id; }
    public int getCantidadArticulos() { return cantidadArticulos; }
    public boolean esRapido() { return rapido; }

    public boolean isAtendido() { return atendido; }       // ✅ NUEVO
    public void setAtendido(boolean atendido) {            // ✅ NUEVO
        this.atendido = atendido;
    }

    public long getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(long tiempoLlegada) {
        if (tiempoLlegada < 0) throw new IllegalArgumentException("El tiempo de llegada no puede ser negativo");
        this.tiempoLlegada = tiempoLlegada;
    }

    public long getTiempoInicioAtencion() { return tiempoInicioAtencion; }
    public void setTiempoInicioAtencion(long tiempoInicioAtencion) {
        if (tiempoInicioAtencion < 0) throw new IllegalArgumentException("El tiempo de inicio de atención no puede ser negativo");
        this.tiempoInicioAtencion = tiempoInicioAtencion;
    }

    public long getTiempoSalida() { return tiempoSalida; }
    public void setTiempoSalida(long tiempoSalida) {
        if (tiempoSalida < 0) throw new IllegalArgumentException("El tiempo de salida no puede ser negativo");
        this.tiempoSalida = tiempoSalida;
    }

    public int getTiempoAtencionReal() { return tiempoAtencionReal; }
    public void setTiempoAtencionReal(int tiempoAtencionReal) {
        if (tiempoAtencionReal < 0) throw new IllegalArgumentException("El tiempo de atención real no puede ser negativo");
        this.tiempoAtencionReal = tiempoAtencionReal;
    }

    public void setRapido(boolean rapido) {
        this.rapido = rapido;
    }

    // ============================================================
    // Métodos de negocio
    // ============================================================

    public long calcularTiempoEspera() {
        if (tiempoLlegada == 0 || tiempoInicioAtencion == 0) {
            return 0;
        }
        return tiempoInicioAtencion - tiempoLlegada;
    }

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