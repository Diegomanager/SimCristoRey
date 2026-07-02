package com.supermercado.domain.cooperativa.model;

public class Socio {
    private String id;
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

    public Socio(String id, ServicioFinanciero servicio) {
        this.id = id;
        this.servicio = servicio;
        this.atendida = false;
        this.prioridad = 2;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }
    public ServicioFinanciero getServicio() { return servicio; }
    public void setServicio(ServicioFinanciero servicio) { this.servicio = servicio; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public int getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(int duracionEstimada) { this.duracionEstimada = duracionEstimada; }
    public long getTiempoLlegada() { return tiempoLlegada; }
    public void setTiempoLlegada(long tiempoLlegada) { this.tiempoLlegada = tiempoLlegada; }
    public long getTiempoInicioAtencion() { return tiempoInicioAtencion; }
    public void setTiempoInicioAtencion(long tiempoInicioAtencion) { this.tiempoInicioAtencion = tiempoInicioAtencion; }
    public long getTiempoSalida() { return tiempoSalida; }
    public void setTiempoSalida(long tiempoSalida) { this.tiempoSalida = tiempoSalida; }
    public boolean isEsPreferente() { return esPreferente; }
    public void setEsPreferente(boolean esPreferente) { this.esPreferente = esPreferente; }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public boolean isAtendida() { return atendida; }
    public void setAtendida(boolean atendida) { this.atendida = atendida; }
}