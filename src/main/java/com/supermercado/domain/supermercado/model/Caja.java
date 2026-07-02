package com.supermercado.domain.supermercado.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Caja {
    private final String id;
    private final boolean esRapida;
    private EstadoCaja estado;
    private Cliente clienteActual;
    private List<Cliente> colaClientes;
    private AtomicInteger totalAtendidos;
    private int colaMaxima;
    private long tiempoOcupado;
    private List<Cliente> clientesAtendidosHistorial;
    private EstadoCaja estadoAnterior;

    public Caja(String id, boolean esRapida) {
        this.id = id;
        this.esRapida = esRapida;
        this.estado = EstadoCaja.LIBRE;
        this.clienteActual = null;
        this.colaClientes = new ArrayList<>();
        this.totalAtendidos = new AtomicInteger(0);
        this.colaMaxima = 0;
        this.tiempoOcupado = 0;
        this.clientesAtendidosHistorial = new ArrayList<>();
        this.estadoAnterior = null;
    }

    public Caja(int id, boolean esRapida) {
        this("CAJA " + id, esRapida);
    }

    public String getId() { return id; }
    public boolean esRapida() { return esRapida; }
    public EstadoCaja getEstado() { return estado; }
    public Cliente getClienteActual() { return clienteActual; }
    public int getClientesEnCola() { return colaClientes.size(); }
    public int getTotalAtendidos() { return totalAtendidos.get(); }
    public int getColaMaxima() { return colaMaxima; }
    public long getTiempoOcupado() { return tiempoOcupado; }
    public List<Cliente> getClientesAtendidos() { return List.copyOf(clientesAtendidosHistorial); }

    public boolean agregarCliente(Cliente cliente) {
        if (estado == EstadoCaja.DETENIDA) {
            return false;
        }
        colaClientes.add(cliente);
        if (colaClientes.size() > colaMaxima) {
            colaMaxima = colaClientes.size();
        }
        return true;
    }

    public Cliente prepararSiguienteCliente() {
        if (estado == EstadoCaja.DETENIDA || estado == EstadoCaja.PAUSADA) {
            return null;
        }
        if (colaClientes.isEmpty()) {
            return null;
        }
        clienteActual = colaClientes.remove(0);
        estado = EstadoCaja.OCUPADA;
        return clienteActual;
    }

    public void finalizarAtencion() {
        if (clienteActual != null) {
            totalAtendidos.incrementAndGet();
            clientesAtendidosHistorial.add(clienteActual);
            clienteActual = null;
            estado = colaClientes.isEmpty() ? EstadoCaja.LIBRE : EstadoCaja.OCUPADA;
            if (!colaClientes.isEmpty()) {
                clienteActual = colaClientes.remove(0);
                estado = EstadoCaja.OCUPADA;
            }
        }
    }

    public void pausar() {
        if (estado != EstadoCaja.DETENIDA && estado != EstadoCaja.PAUSADA) {
            estadoAnterior = estado;
            estado = EstadoCaja.PAUSADA;
        }
    }

    public void reanudar() {
        if (estado == EstadoCaja.PAUSADA) {
            if (estadoAnterior != null) {
                estado = estadoAnterior;
                estadoAnterior = null;
            } else if (clienteActual != null) {
                estado = EstadoCaja.OCUPADA;
            } else {
                estado = EstadoCaja.LIBRE;
            }
        }
    }

    public void detener() {
        estado = EstadoCaja.DETENIDA;
    }

    public void reiniciar() {
        estado = EstadoCaja.LIBRE;
        clienteActual = null;
        colaClientes.clear();
        totalAtendidos.set(0);
        colaMaxima = 0;
        tiempoOcupado = 0;
        clientesAtendidosHistorial.clear();
        estadoAnterior = null;
    }

    public boolean estaActiva() {
        return estado != EstadoCaja.DETENIDA;
    }

    public boolean estaOcupada() {
        return estado == EstadoCaja.OCUPADA;
    }

    public boolean tieneClientesPendientes() {
        return clienteActual != null || !colaClientes.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s [%s, %d en cola, %d atendidos]", id, estado, getClientesEnCola(), getTotalAtendidos());
    }
}