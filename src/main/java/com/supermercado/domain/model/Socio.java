package com.supermercado.domain.model;

import java.util.Objects;

/**
 * Representa a un socio que solicita un servicio en la cooperativa.
 * Reemplaza a Cliente.
 */
public class Socio {
    private final String id;
    private String ficha;
    private ServicioFinanciero servicio;
    private double monto;
    private int duracionEstimada;
    private long tiempoLlegada;
    private long tiempoInicioAtencion;
    private long tiempoSalida;
    private boolean esPreferente;
    private int prioridad;
    private boolean atendida;

    public Socio(String id) {
        this.id = Objects.requireNonNull(id, "ID no puede ser nulo");
        this.prioridad = 2;
        this.esPreferente = false;
        this.atendida = false;
        this.monto = 0.0;
        this.duracionEstimada = 1;
    }
    public Socio(int id) { this("SOC-" + id); }

    public String getId() { return id; }
    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }
    public ServicioFinanciero getServicio() { return servicio; }
    public void setServicio(ServicioFinanciero servicio) {
        this.servicio = servicio;
        if (servicio != null) {
            this.monto = servicio.generarMontoAleatorio();
            this.duracionEstimada = servicio.generarDuracionAleatoria();
        }
    }
    public double getMonto() { return monto; }
    public void setMonto(double monto) {
        if (monto < 0) throw new IllegalArgumentException("El monto no puede ser negativo");
        this.monto = monto;
    }
    public int getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(int duracionEstimada) {
        if (duracionEstimada < 0) throw new IllegalArgumentException("La duración estimada no puede ser negativa");
        this.duracionEstimada = duracionEstimada;
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
    public boolean isEsPreferente() { return esPreferente; }
    public void setEsPreferente(boolean esPreferente) {
        this.esPreferente = esPreferente;
        this.prioridad = esPreferente ? 1 : 2;
    }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > 3) throw new IllegalArgumentException("La prioridad debe ser 1 (alta), 2 (media) o 3 (baja)");
        this.prioridad = prioridad;
    }
    public boolean isAtendida() { return atendida; }
    public void setAtendida(boolean atendida) { this.atendida = atendida; }

    public long calcularTiempoEspera() {
        if (tiempoLlegada == 0 || tiempoInicioAtencion == 0) return 0;
        return tiempoInicioAtencion - tiempoLlegada;
    }
    public long calcularTiempoTotal() {
        if (tiempoLlegada == 0 || tiempoSalida == 0) return 0;
        return tiempoSalida - tiempoLlegada;
    }
    public double calcularInteresGenerado() {
        if (servicio == null || monto == 0 || duracionEstimada == 0) return 0.0;
        return monto * servicio.getTasaInteres() * (duracionEstimada / 365.0) / 100.0;
    }

    @Override
    public String toString() {
        String serv = servicio != null ? servicio.getNombre() : "Sin servicio";
        return String.format("%s [%s] %.2f Bs (%d min)", id, serv, monto, duracionEstimada);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Socio socio = (Socio) o;
        return Objects.equals(id, socio.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}
