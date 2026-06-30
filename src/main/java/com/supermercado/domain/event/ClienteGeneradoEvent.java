package com.supermercado.domain.event;

import com.supermercado.domain.model.Cliente;

/**
 * Evento que se dispara cuando un nuevo cliente es generado en la simulación.
 * Contiene el cliente generado y la hora simulada en que ocurrió.
 */
public class ClienteGeneradoEvent {
    private final Cliente cliente;
    private final String hora;

    public ClienteGeneradoEvent(Cliente cliente, String hora) {
        this.cliente = cliente;
        this.hora = hora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getHora() {
        return hora;
    }
}