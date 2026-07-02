package com.supermercado.application.cooperativa.dto;

import java.util.List;

public class ConfiguracionCooperativaDTO {
    private int duracionPrincipal; // en minutos
    private int numCajas;
    private List<ServicioDTO> servicios;
    private List<TipoCajaDTO> tiposCaja;
    private int capacidadMaximaSocios;

    public ConfiguracionCooperativaDTO() {
        this.duracionPrincipal = 480; // 8 horas por defecto
        this.numCajas = 5;
        this.capacidadMaximaSocios = 400;
    }

    public int getDuracionPrincipal() { return duracionPrincipal; }
    public void setDuracionPrincipal(int duracionPrincipal) { this.duracionPrincipal = duracionPrincipal; }

    public int getNumCajas() { return numCajas; }
    public void setNumCajas(int numCajas) { this.numCajas = numCajas; }

    public List<ServicioDTO> getServicios() { return servicios; }
    public void setServicios(List<ServicioDTO> servicios) { this.servicios = servicios; }

    public List<TipoCajaDTO> getTiposCaja() { return tiposCaja; }
    public void setTiposCaja(List<TipoCajaDTO> tiposCaja) { this.tiposCaja = tiposCaja; }

    public int getCapacidadMaximaSocios() { return capacidadMaximaSocios; }
    public void setCapacidadMaximaSocios(int capacidadMaximaSocios) { this.capacidadMaximaSocios = capacidadMaximaSocios; }
}