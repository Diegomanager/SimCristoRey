package com.supermercado.application.dto;

public class ConfiguracionDTO {
    
    private final int horasSimuladas;
    private final int duracionRealSegundos;
    private final int numCajasNormales;
    private final int numCajasRapidas;
    private final int limiteClienteRapido;
    private final int probabilidadLlegadaCliente;
    private final int limiteClientes;
    private final int tiempoCajaNormalMin;
    private final int tiempoCajaNormalMax;
    private final int tiempoCajaRapidaMin;
    private final int tiempoCajaRapidaMax;
    private final int articulosClienteMin;
    private final int articulosClienteMax;
    private final boolean mostrarDetalleClientes;
    private final boolean mostrarEstadisticasAvanzadas;

    private ConfiguracionDTO(Builder builder) {
        this.horasSimuladas = builder.horasSimuladas;
        this.duracionRealSegundos = builder.duracionRealSegundos;
        this.numCajasNormales = builder.numCajasNormales;
        this.numCajasRapidas = builder.numCajasRapidas;
        this.limiteClienteRapido = builder.limiteClienteRapido;
        this.probabilidadLlegadaCliente = builder.probabilidadLlegadaCliente;
        this.limiteClientes = builder.limiteClientes;
        this.tiempoCajaNormalMin = builder.tiempoCajaNormalMin;
        this.tiempoCajaNormalMax = builder.tiempoCajaNormalMax;
        this.tiempoCajaRapidaMin = builder.tiempoCajaRapidaMin;
        this.tiempoCajaRapidaMax = builder.tiempoCajaRapidaMax;
        this.articulosClienteMin = builder.articulosClienteMin;
        this.articulosClienteMax = builder.articulosClienteMax;
        this.mostrarDetalleClientes = builder.mostrarDetalleClientes;
        this.mostrarEstadisticasAvanzadas = builder.mostrarEstadisticasAvanzadas;
    }

    public int getHorasSimuladas() { return horasSimuladas; }
    public int getDuracionRealSegundos() { return duracionRealSegundos; }
    public int getNumCajasNormales() { return numCajasNormales; }
    public int getNumCajasRapidas() { return numCajasRapidas; }
    public int getLimiteClienteRapido() { return limiteClienteRapido; }
    public int getProbabilidadLlegadaCliente() { return probabilidadLlegadaCliente; }
    public int getLimiteClientes() { return limiteClientes; }
    public int getTiempoCajaNormalMin() { return tiempoCajaNormalMin; }
    public int getTiempoCajaNormalMax() { return tiempoCajaNormalMax; }
    public int getTiempoCajaRapidaMin() { return tiempoCajaRapidaMin; }
    public int getTiempoCajaRapidaMax() { return tiempoCajaRapidaMax; }
    public int getArticulosClienteMin() { return articulosClienteMin; }
    public int getArticulosClienteMax() { return articulosClienteMax; }
    public boolean isMostrarDetalleClientes() { return mostrarDetalleClientes; }
    public boolean isMostrarEstadisticasAvanzadas() { return mostrarEstadisticasAvanzadas; }

    public static class Builder {
        private int horasSimuladas = 12;
        private int duracionRealSegundos = 20;
        private int numCajasNormales = 2;
        private int numCajasRapidas = 1;
        private int limiteClienteRapido = 10;
        private int probabilidadLlegadaCliente = 40;
        private int limiteClientes = 0;
        private int tiempoCajaNormalMin = 4;
        private int tiempoCajaNormalMax = 8;
        private int tiempoCajaRapidaMin = 2;
        private int tiempoCajaRapidaMax = 5;
        private int articulosClienteMin = 1;
        private int articulosClienteMax = 30;
        private boolean mostrarDetalleClientes = true;
        private boolean mostrarEstadisticasAvanzadas = true;

        public Builder horasSimuladas(int horas) { this.horasSimuladas = horas; return this; }
        public Builder duracionRealSegundos(int segundos) { this.duracionRealSegundos = segundos; return this; }
        public Builder numCajasNormales(int cajas) { this.numCajasNormales = cajas; return this; }
        public Builder numCajasRapidas(int cajas) { this.numCajasRapidas = cajas; return this; }
        public Builder limiteClienteRapido(int limite) { this.limiteClienteRapido = limite; return this; }
        public Builder probabilidadLlegadaCliente(int prob) { this.probabilidadLlegadaCliente = prob; return this; }
        public Builder limiteClientes(int limite) { this.limiteClientes = limite; return this; }
        public Builder tiempoCajaNormalMin(int min) { this.tiempoCajaNormalMin = min; return this; }
        public Builder tiempoCajaNormalMax(int max) { this.tiempoCajaNormalMax = max; return this; }
        public Builder tiempoCajaRapidaMin(int min) { this.tiempoCajaRapidaMin = min; return this; }
        public Builder tiempoCajaRapidaMax(int max) { this.tiempoCajaRapidaMax = max; return this; }
        public Builder articulosClienteMin(int min) { this.articulosClienteMin = min; return this; }
        public Builder articulosClienteMax(int max) { this.articulosClienteMax = max; return this; }
        public Builder mostrarDetalleClientes(boolean mostrar) { this.mostrarDetalleClientes = mostrar; return this; }
        public Builder mostrarEstadisticasAvanzadas(boolean mostrar) { this.mostrarEstadisticasAvanzadas = mostrar; return this; }

        public ConfiguracionDTO build() {
            return new ConfiguracionDTO(this);
        }
    }
}
