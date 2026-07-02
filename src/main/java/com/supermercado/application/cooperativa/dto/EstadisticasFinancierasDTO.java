package com.supermercado.application.cooperativa.dto;

import java.util.Map;

public class EstadisticasFinancierasDTO {
    private int totalSociosGenerados;
    private int totalSociosAtendidos;
    private int sociosRezagados;
    private int sociosEnEspera;
    private double montoTotalAtendido;
    private double tiempoPromedioAtencion; // segundos
    private double tiempoPromedioEspera;   // segundos
    private Map<String, Double> montosPorServicio;
    private Map<String, Integer> cantidadPorServicio;
    private Map<String, Double> interesesPorServicio;
    private boolean faseRezagadosActiva;

    // Getters y setters
    public int getTotalSociosGenerados() { return totalSociosGenerados; }
    public void setTotalSociosGenerados(int totalSociosGenerados) { this.totalSociosGenerados = totalSociosGenerados; }

    public int getTotalSociosAtendidos() { return totalSociosAtendidos; }
    public void setTotalSociosAtendidos(int totalSociosAtendidos) { this.totalSociosAtendidos = totalSociosAtendidos; }

    public int getSociosRezagados() { return sociosRezagados; }
    public void setSociosRezagados(int sociosRezagados) { this.sociosRezagados = sociosRezagados; }

    public int getSociosEnEspera() { return sociosEnEspera; }
    public void setSociosEnEspera(int sociosEnEspera) { this.sociosEnEspera = sociosEnEspera; }

    public double getMontoTotalAtendido() { return montoTotalAtendido; }
    public void setMontoTotalAtendido(double montoTotalAtendido) { this.montoTotalAtendido = montoTotalAtendido; }

    public double getTiempoPromedioAtencion() { return tiempoPromedioAtencion; }
    public void setTiempoPromedioAtencion(double tiempoPromedioAtencion) { this.tiempoPromedioAtencion = tiempoPromedioAtencion; }

    public double getTiempoPromedioEspera() { return tiempoPromedioEspera; }
    public void setTiempoPromedioEspera(double tiempoPromedioEspera) { this.tiempoPromedioEspera = tiempoPromedioEspera; }

    public Map<String, Double> getMontosPorServicio() { return montosPorServicio; }
    public void setMontosPorServicio(Map<String, Double> montosPorServicio) { this.montosPorServicio = montosPorServicio; }

    public Map<String, Integer> getCantidadPorServicio() { return cantidadPorServicio; }
    public void setCantidadPorServicio(Map<String, Integer> cantidadPorServicio) { this.cantidadPorServicio = cantidadPorServicio; }

    public Map<String, Double> getInteresesPorServicio() { return interesesPorServicio; }
    public void setInteresesPorServicio(Map<String, Double> interesesPorServicio) { this.interesesPorServicio = interesesPorServicio; }

    public boolean isFaseRezagadosActiva() { return faseRezagadosActiva; }
    public void setFaseRezagadosActiva(boolean faseRezagadosActiva) { this.faseRezagadosActiva = faseRezagadosActiva; }
}