package com.supermercado.application.cooperativa.dto;

import java.util.List;

public class ConfiguracionCooperativaDTO {
    private int duracionPrincipal;
    private int numCajas;
    private List<ServicioDTO> servicios;
    private List<TipoCajaDTO> tiposCaja;
    private int capacidadMaximaSocios;
    private int horaApertura;

    public ConfiguracionCooperativaDTO() {
        this.duracionPrincipal = 480;
        this.numCajas = 5;
        this.capacidadMaximaSocios = 400;
        this.horaApertura = 8;
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

    public int getHoraApertura() { return horaApertura; }
    public void setHoraApertura(int horaApertura) { this.horaApertura = horaApertura; }
}