package com.supermercado.domain.cooperativa.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SalaEspera {
    private final Queue<Socio> colaPreferentes = new LinkedList<>();
    private final Queue<Socio> colaNormales    = new LinkedList<>();
    private int totalAtendidos = 0;

    public void agregarSocio(Socio s) {
        if (s == null) return;
        if (s.isEsPreferente()) colaPreferentes.offer(s);
        else                    colaNormales.offer(s);
    }

    public Socio verSiguiente() {
        if (!colaPreferentes.isEmpty()) return colaPreferentes.peek();
        return colaNormales.peek();
    }

    public Socio siguienteSocio() {
        if (!colaPreferentes.isEmpty()) return colaPreferentes.poll();
        return colaNormales.poll();
    }

    public boolean isEmpty() { return colaPreferentes.isEmpty() && colaNormales.isEmpty(); }
    public int getTotalEsperando() { return colaPreferentes.size() + colaNormales.size(); }
    public int getTotalPreferentesEsperando() { return colaPreferentes.size(); }
    public int getTotalNormalesEsperando()     { return colaNormales.size(); }

    public void incrementarAtendidos() { totalAtendidos++; }
    public int  getTotalAtendidos()    { return totalAtendidos; }

    public void reiniciar() {
        colaPreferentes.clear(); colaNormales.clear(); totalAtendidos = 0;
    }

    public List<Socio> getSocios() {
        List<Socio> lista = new ArrayList<>(colaPreferentes);
        lista.addAll(colaNormales);
        return lista;
    }
}