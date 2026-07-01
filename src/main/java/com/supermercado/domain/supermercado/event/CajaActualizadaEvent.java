package com.supermercado.domain.supermercado.event;

/**
 * Evento que se dispara cuando el estado de una caja cambia.
 * Incluye el número de caja, su nuevo estado, la cantidad de clientes en cola,
 * el ID del cliente que está siendo atendido (si existe) y el tipo de caja (R/N).
 */
public class CajaActualizadaEvent {
    private final int numCaja;
    private final String estado;
    private final int cola;
    private final String clienteInfo;
    private final String tipo;

    public CajaActualizadaEvent(int numCaja, String estado, int cola, String clienteInfo, String tipo) {
        this.numCaja = numCaja;
        this.estado = estado;
        this.cola = cola;
        this.clienteInfo = clienteInfo;
        this.tipo = tipo;
    }

    public int getNumCaja() {
        return numCaja;
    }

    public String getEstado() {
        return estado;
    }

    public int getCola() {
        return cola;
    }

    public String getClienteInfo() {
        return clienteInfo;
    }

    public String getTipo() {
        return tipo;
    }
}