package com.supermercado.domain.supermercado.model;

import java.util.ArrayList;
import java.util.List;

public class Configuracion {
    private final int numCajasNormales;
    private final int numCajasRapidas;
    private final int horasSimuladas;
    private final int duracionRealSegundos;
    private final int probabilidadLlegadaCliente;
    private final int limiteClientes;
    private final int articulosClienteMin;
    private final int articulosClienteMax;
    private final int tiempoCajaNormalMin;
    private final int tiempoCajaNormalMax;
    private final int tiempoCajaRapidaMin;
    private final int tiempoCajaRapidaMax;
    private final List<Caja> cajas;

    private Configuracion(Builder builder) {
        this.numCajasNormales = builder.numCajasNormales;
        this.numCajasRapidas = builder.numCajasRapidas;
        this.horasSimuladas = builder.horasSimuladas;
        this.duracionRealSegundos = builder.duracionRealSegundos;
        this.probabilidadLlegadaCliente = builder.probabilidadLlegadaCliente;
        this.limiteClientes = builder.limiteClientes;
        this.articulosClienteMin = builder.articulosClienteMin;
        this.articulosClienteMax = builder.articulosClienteMax;
        this.tiempoCajaNormalMin = builder.tiempoCajaNormalMin;
        this.tiempoCajaNormalMax = builder.tiempoCajaNormalMax;
        this.tiempoCajaRapidaMin = builder.tiempoCajaRapidaMin;
        this.tiempoCajaRapidaMax = builder.tiempoCajaRapidaMax;
        this.cajas = new ArrayList<>();
    }

    public static class Builder {
        private int numCajasNormales = 6;
        private int numCajasRapidas = 2;
        private int horasSimuladas = 10;
        private int duracionRealSegundos = 30;
        private int probabilidadLlegadaCliente = 50;
        private int limiteClientes = 0;
        private int articulosClienteMin = 1;
        private int articulosClienteMax = 20;
        private int tiempoCajaNormalMin = 2;
        private int tiempoCajaNormalMax = 8;
        private int tiempoCajaRapidaMin = 1;
        private int tiempoCajaRapidaMax = 5;

        public Builder numCajasNormales(int value) { this.numCajasNormales = value; return this; }
        public Builder numCajasRapidas(int value) { this.numCajasRapidas = value; return this; }
        public Builder horasSimuladas(int value) { this.horasSimuladas = value; return this; }
        public Builder duracionRealSegundos(int value) { this.duracionRealSegundos = value; return this; }
        public Builder probabilidadLlegadaCliente(int value) { this.probabilidadLlegadaCliente = value; return this; }
        public Builder limiteClientes(int value) { this.limiteClientes = value; return this; }
        public Builder articulosClienteMin(int value) { this.articulosClienteMin = value; return this; }
        public Builder articulosClienteMax(int value) { this.articulosClienteMax = value; return this; }
        public Builder tiempoCajaNormalMin(int value) { this.tiempoCajaNormalMin = value; return this; }
        public Builder tiempoCajaNormalMax(int value) { this.tiempoCajaNormalMax = value; return this; }
        public Builder tiempoCajaRapidaMin(int value) { this.tiempoCajaRapidaMin = value; return this; }
        public Builder tiempoCajaRapidaMax(int value) { this.tiempoCajaRapidaMax = value; return this; }

        public Configuracion build() {
            return new Configuracion(this);
        }
    }

    public int getNumCajasNormales() { return numCajasNormales; }
    public int getNumCajasRapidas() { return numCajasRapidas; }
    public int getHorasSimuladas() { return horasSimuladas; }
    public int getDuracionRealSegundos() { return duracionRealSegundos; }
    public int getProbabilidadLlegadaCliente() { return probabilidadLlegadaCliente; }
    public int getLimiteClientes() { return limiteClientes; }
    public int getArticulosClienteMin() { return articulosClienteMin; }
    public int getArticulosClienteMax() { return articulosClienteMax; }
    public int getTiempoCajaNormalMin() { return tiempoCajaNormalMin; }
    public int getTiempoCajaNormalMax() { return tiempoCajaNormalMax; }
    public int getTiempoCajaRapidaMin() { return tiempoCajaRapidaMin; }
    public int getTiempoCajaRapidaMax() { return tiempoCajaRapidaMax; }
    public List<Caja> getCajas() { return cajas; }
    
    public double getClientesPorHora() {
        return 60.0 * 60 / (getTiempoCajaNormalMin() + getTiempoCajaRapidaMin());
    }
    
    public double getTiempoAtencionPromedio() {
        return (getTiempoCajaNormalMin() + getTiempoCajaNormalMax() + 
                getTiempoCajaRapidaMin() + getTiempoCajaRapidaMax()) / 4.0;
    }
}