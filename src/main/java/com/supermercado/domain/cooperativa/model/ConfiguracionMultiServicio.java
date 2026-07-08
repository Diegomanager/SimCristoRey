package com.supermercado.domain.cooperativa.model;

public class ConfiguracionMultiServicio {
    private double probabilidadMultiple  = 0.25;
    private int    maxServiciosPorSocio  = 4;
    private int    tiempoExtraPorServicio= 3;

    public ConfiguracionMultiServicio() {}
    public ConfiguracionMultiServicio(double prob, int max, int extra) {
        this.probabilidadMultiple   = Math.max(0, Math.min(1, prob));
        this.maxServiciosPorSocio   = Math.max(1, max);
        this.tiempoExtraPorServicio = Math.max(0, extra);
    }
    public double getProbabilidadMultiple()   { return probabilidadMultiple; }
    public int    getMaxServiciosPorSocio()   { return maxServiciosPorSocio; }
    public int    getTiempoExtraPorServicio() { return tiempoExtraPorServicio; }
    public void setProbabilidadMultiple(double v)  { this.probabilidadMultiple   = Math.max(0, Math.min(1,v)); }
    public void setMaxServiciosPorSocio(int v)     { this.maxServiciosPorSocio   = Math.max(1,v); }
    public void setTiempoExtraPorServicio(int v)   { this.tiempoExtraPorServicio = Math.max(0,v); }
}