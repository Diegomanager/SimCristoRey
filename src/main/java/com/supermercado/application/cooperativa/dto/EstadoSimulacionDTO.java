package com.supermercado.application.cooperativa.dto;

import java.util.List;

public class EstadoSimulacionDTO {
    private boolean ejecutando;
    private boolean pausado;
    private boolean detenido;
    private boolean faseRezagados;
    private int sociosGenerados;
    private int sociosAtendidos;
    private int sociosRezagados;
    private int sociosEnEspera;
    private List<CajaDTO> cajas;

    // Getters y setters
    public boolean isEjecutando() { return ejecutando; }
    public void setEjecutando(boolean ejecutando) { this.ejecutando = ejecutando; }

    public boolean isPausado() { return pausado; }
    public void setPausado(boolean pausado) { this.pausado = pausado; }

    public boolean isDetenido() { return detenido; }
    public void setDetenido(boolean detenido) { this.detenido = detenido; }

    public boolean isFaseRezagados() { return faseRezagados; }
    public void setFaseRezagados(boolean faseRezagados) { this.faseRezagados = faseRezagados; }

    public int getSociosGenerados() { return sociosGenerados; }
    public void setSociosGenerados(int sociosGenerados) { this.sociosGenerados = sociosGenerados; }

    public int getSociosAtendidos() { return sociosAtendidos; }
    public void setSociosAtendidos(int sociosAtendidos) { this.sociosAtendidos = sociosAtendidos; }

    public int getSociosRezagados() { return sociosRezagados; }
    public void setSociosRezagados(int sociosRezagados) { this.sociosRezagados = sociosRezagados; }

    public int getSociosEnEspera() { return sociosEnEspera; }
    public void setSociosEnEspera(int sociosEnEspera) { this.sociosEnEspera = sociosEnEspera; }

    public List<CajaDTO> getCajas() { return cajas; }
    public void setCajas(List<CajaDTO> cajas) { this.cajas = cajas; }
}