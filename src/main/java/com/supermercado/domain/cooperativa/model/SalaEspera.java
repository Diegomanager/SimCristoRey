package com.supermercado.domain.cooperativa.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SalaEspera {
    private Queue<Socio> colaUnica;
    private int totalSociosAtendidos;

    public SalaEspera() {
        this.colaUnica = new LinkedList<>();
        this.totalSociosAtendidos = 0;
    }

    public void agregarSocio(Socio socio) {
        colaUnica.offer(socio);
    }

    public Socio siguienteSocio() {
        return colaUnica.poll();
    }

    // NUEVO: verSiguiente (peek)
    public Socio verSiguiente() {
        return colaUnica.peek();
    }

    public boolean isEmpty() {
        return colaUnica.isEmpty();
    }

    public int getTotalEsperando() {
        return colaUnica.size();
    }

    public int getTotalAtendidos() {
        return totalSociosAtendidos;
    }

    public void incrementarAtendidos() {
        totalSociosAtendidos++;
    }

    public void reiniciar() {
        colaUnica.clear();
        totalSociosAtendidos = 0;
    }

    public List<Socio> getSociosEnEspera() {
        return new LinkedList<>(colaUnica);
    }
}