package com.supermercado.domain.cooperativa.model;

/**
 * Bloque horario dentro de una jornada.
 * inicio y fin en minutos desde 00:00 (ej. 8:30 = 510, 12:00 = 720).
 */
public class BloqueHorario {
    private int inicio; // minutos desde 00:00
    private int fin;

    public BloqueHorario() {}
    public BloqueHorario(int inicio, int fin) {
        if (inicio >= fin) throw new IllegalArgumentException("inicio debe ser menor que fin");
        this.inicio = inicio;
        this.fin    = fin;
    }

    public int getDuracion() { return fin - inicio; }

    public boolean estaActivo(int minutosDesdeMedanoche) {
        return minutosDesdeMedanoche >= inicio && minutosDesdeMedanoche < fin;
    }

    public String toHHMM(int minutos) {
        return String.format("%02d:%02d", minutos / 60, minutos % 60);
    }

    @Override
    public String toString() {
        return toHHMM(inicio) + "–" + toHHMM(fin) + " (" + getDuracion() + " min)";
    }

    public int getInicio() { return inicio; }
    public void setInicio(int v){ this.inicio = v; }
    public int getFin()    { return fin; }
    public void setFin(int v)  { this.fin = v; }
}