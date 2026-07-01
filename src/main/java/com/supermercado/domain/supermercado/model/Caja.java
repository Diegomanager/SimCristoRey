package com.supermercado.domain.supermercado.model;

public class Caja {
    private String id;
    private EstadoCaja estado;
    private Cliente clienteActual;
    private int totalAtendidos;
    private boolean activa;

    public Caja(String id) {
        this.id = id;
        this.estado = EstadoCaja.LIBRE;
        this.activa = true;
        this.totalAtendidos = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public EstadoCaja getEstado() { return estado; }
    public void setEstado(EstadoCaja estado) { this.estado = estado; }

    public Cliente getClienteActual() { return clienteActual; }
    public void setClienteActual(Cliente clienteActual) { this.clienteActual = clienteActual; }

    public int getTotalAtendidos() { return totalAtendidos; }
    public void incrementarAtendidos() { this.totalAtendidos++; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public void asignarCliente(Cliente cliente) {
        this.clienteActual = cliente;
        this.estado = EstadoCaja.OCUPADA;
    }

    public void finalizarAtencion() {
        if (clienteActual != null) {
            clienteActual.setAtendido(true);
            this.totalAtendidos++;
            this.clienteActual = null;
        }
        this.estado = EstadoCaja.LIBRE;
    }

    public void pausar() {
        if (estado == EstadoCaja.OCUPADA || estado == EstadoCaja.LIBRE) {
            this.estado = EstadoCaja.PAUSADA;
        }
    }

    public void reanudar() {
        if (estado == EstadoCaja.PAUSADA) {
            this.estado = EstadoCaja.LIBRE;
        }
    }

    public void detener() {
        this.estado = EstadoCaja.DETENIDA;
        this.clienteActual = null;
    }
}