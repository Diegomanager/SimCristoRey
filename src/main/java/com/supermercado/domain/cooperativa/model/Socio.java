package com.supermercado.domain.cooperativa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Socio {
    private String  id, ficha, tipoCajaDestino;
    private ServicioFinanciero servicio;
    private List<ServicioFinanciero> serviciosAdicionales = new ArrayList<>();
    private double  monto;
    private int     duracionEstimada;
    private long    tiempoLlegada, tiempoInicioAtencion, tiempoSalida;
    private boolean esPreferente;
    private int     prioridad = 3;
    private boolean atendida  = false;

    public boolean isMultiServicio() {
        return serviciosAdicionales != null && !serviciosAdicionales.isEmpty();
    }
    public int getTotalServicios() {
        return 1 + (serviciosAdicionales != null ? serviciosAdicionales.size() : 0);
    }
    public List<ServicioFinanciero> getTodosLosServicios() {
        List<ServicioFinanciero> todos = new ArrayList<>();
        if (servicio != null) todos.add(servicio);
        if (serviciosAdicionales != null) todos.addAll(serviciosAdicionales);
        return Collections.unmodifiableList(todos);
    }
    public void agregarServicioAdicional(ServicioFinanciero s) {
        if (s != null) serviciosAdicionales.add(s);
    }
    public String getDescripcionServicios() {
        if (!isMultiServicio()) return servicio != null ? servicio.getNombre() : "General";
        StringBuilder sb = new StringBuilder();
        for (ServicioFinanciero s : getTodosLosServicios()) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(s.getNombre());
        }
        return sb.toString();
    }
    // Getters/Setters
    public String  getId() { return id; }
    public void    setId(String id) { this.id = id; }
    public String  getFicha() { return ficha; }
    public void    setFicha(String f) { this.ficha = f; }
    public String  getTipoCajaDestino() { return tipoCajaDestino; }
    public void    setTipoCajaDestino(String t) { this.tipoCajaDestino = t; }
    public ServicioFinanciero getServicio() { return servicio; }
    public void setServicio(ServicioFinanciero s) { this.servicio = s; }
    public List<ServicioFinanciero> getServiciosAdicionales() { return serviciosAdicionales; }
    public void setServiciosAdicionales(List<ServicioFinanciero> lista) { this.serviciosAdicionales = lista; }
    public double  getMonto() { return monto; }
    public void    setMonto(double m) { this.monto = m; }
    public int     getDuracionEstimada() { return duracionEstimada; }
    public void    setDuracionEstimada(int d) { this.duracionEstimada = d; }
    public long    getTiempoLlegada() { return tiempoLlegada; }
    public void    setTiempoLlegada(long t) { this.tiempoLlegada = t; }
    public long    getTiempoInicioAtencion() { return tiempoInicioAtencion; }
    public void    setTiempoInicioAtencion(long t) { this.tiempoInicioAtencion = t; }
    public long    getTiempoSalida() { return tiempoSalida; }
    public void    setTiempoSalida(long t) { this.tiempoSalida = t; }
    public boolean isEsPreferente() { return esPreferente; }
    public void    setEsPreferente(boolean v) { this.esPreferente = v; }
    public int     getPrioridad() { return prioridad; }
    public void    setPrioridad(int p) { this.prioridad = p; }
    public boolean isAtendida() { return atendida; }
    public void    setAtendida(boolean v) { this.atendida = v; }
}