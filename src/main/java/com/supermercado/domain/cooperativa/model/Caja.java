package com.supermercado.domain.cooperativa.model;

import java.util.ArrayList;
import java.util.List;

public class Caja {
    private String id;
    private TipoCaja tipo;
    private EstadoCaja estado;
    private Socio socioActual;
    private boolean activa;
    private int totalAtendidos;
    private double montoTotalAtendido;
    private List<Socio> sociosAtendidos;

    public Caja(String id, TipoCaja tipo) {
        this.id = id;
        this.tipo = tipo;
        this.estado = EstadoCaja.LIBRE;
        this.activa = true;
        this.totalAtendidos = 0;
        this.montoTotalAtendido = 0.0;
        this.sociosAtendidos = new ArrayList<>();
    }

    // Constructor sin argumentos (para compatibilidad)
    public Caja() {
        this("C-000", new TipoCaja("GENERAL", "General", "GEN"));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoCaja getTipo() { return tipo; }
    public void setTipo(TipoCaja tipo) { this.tipo = tipo; }

    public EstadoCaja getEstado() { return estado; }
    public void setEstado(EstadoCaja estado) { this.estado = estado; }

    public Socio getSocioActual() { return socioActual; }
    public void setSocioActual(Socio socioActual) { this.socioActual = socioActual; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public int getTotalAtendidos() { return totalAtendidos; }
    public void setTotalAtendidos(int totalAtendidos) { this.totalAtendidos = totalAtendidos; }

    public double getMontoTotalAtendido() { return montoTotalAtendido; }
    public void setMontoTotalAtendido(double montoTotalAtendido) { this.montoTotalAtendido = montoTotalAtendido; }

    public List<Socio> getSociosAtendidos() { return sociosAtendidos; }

    // Método original (sin tiempo) - se mantiene
    public void asignarSocio(Socio socio) {
        this.socioActual = socio;
        this.estado = EstadoCaja.OCUPADA;
        socio.setTiempoInicioAtencion(System.currentTimeMillis());
    }

    // NUEVO: con tiempo (para el motor)
    public void asignarSocio(Socio socio, long tiempoActual) {
        this.socioActual = socio;
        this.estado = EstadoCaja.OCUPADA;
        socio.setTiempoInicioAtencion(tiempoActual);
    }

    // Método original (sin tiempo)
    public void finalizarAtencion() {
        if (socioActual != null) {
            socioActual.setAtendida(true);
            socioActual.setTiempoSalida(System.currentTimeMillis());
            this.totalAtendidos++;
            this.montoTotalAtendido += socioActual.getMonto();
            this.sociosAtendidos.add(socioActual);
            this.socioActual = null;
        }
        this.estado = EstadoCaja.LIBRE;
    }

    // NUEVO: con tiempo (para el motor)
    public void finalizarAtencion(long tiempoActual) {
        if (socioActual != null) {
            socioActual.setAtendida(true);
            socioActual.setTiempoSalida(tiempoActual);
            this.totalAtendidos++;
            this.montoTotalAtendido += socioActual.getMonto();
            this.sociosAtendidos.add(socioActual);
            this.socioActual = null;
        }
        this.estado = EstadoCaja.LIBRE;
    }

    public void pausar() {
        if (estado == EstadoCaja.OCUPADA || estado == EstadoCaja.LIBRE) {
            this.estado = EstadoCaja.PAUSADA;
        }
    }

    public void reanudar() {
        if (estado == EstadoCaja.PAUSADA) {
            this.estado = EstadoCaja.LIBRE;
        }
    }

    public void detener() {
        this.estado = EstadoCaja.DETENIDA;
        this.socioActual = null;
    }

    public void reiniciar() {
        this.estado = EstadoCaja.LIBRE;
        this.socioActual = null;
        this.totalAtendidos = 0;
        this.montoTotalAtendido = 0.0;
        this.sociosAtendidos.clear();
        this.activa = true;
    }
}