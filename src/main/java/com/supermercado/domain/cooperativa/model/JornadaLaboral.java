package com.supermercado.domain.cooperativa.model;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JornadaLaboral {
    private int                 dia;
    private boolean             laborable = true;
    private List<BloqueHorario> bloques   = new ArrayList<>();
    private LocalDate           fechaReal = null;

    public JornadaLaboral() {}
    public JornadaLaboral(int dia, boolean laborable) {
        this.dia = dia; this.laborable = laborable;
    }

    public static JornadaLaboral continua(int dia, int inicio, int fin) {
        JornadaLaboral j = new JornadaLaboral(dia, true);
        j.agregarBloque(new BloqueHorario(inicio, fin)); return j;
    }

    public static JornadaLaboral partida(int dia, int iM, int fM, int iT, int fT) {
        JornadaLaboral j = new JornadaLaboral(dia, true);
        j.agregarBloque(new BloqueHorario(iM, fM));
        j.agregarBloque(new BloqueHorario(iT, fT)); return j;
    }

    public void agregarBloque(BloqueHorario b) { if (b != null) bloques.add(b); }

    public int getTotalMinutosLaborables() {
        return bloques.stream().mapToInt(BloqueHorario::getDuracion).sum();
    }

    public int getMinutoInicio() { return bloques.isEmpty() ? 510 : bloques.get(0).getInicio(); }
    public int getMinutoFin()    { return bloques.isEmpty() ? 990 : bloques.get(bloques.size()-1).getFin(); }

    public String getNombreCompleto() {
        if (fechaReal != null) {
            String nombreDia = fechaReal.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, new Locale("es", "BO"));
            nombreDia = Character.toUpperCase(nombreDia.charAt(0)) + nombreDia.substring(1);
            return "Día " + dia + " (" + nombreDia + ", " + fechaReal + ")";
        }
        return "Día " + dia;
    }

    public int  getDia()                     { return dia; }
    public void setDia(int d)               { this.dia = d; }
    public boolean isLaborable()            { return laborable; }
    public void    setLaborable(boolean v)  { this.laborable = v; }
    public List<BloqueHorario> getBloques() { return bloques; }
    public void setBloques(List<BloqueHorario> b) { this.bloques = b; }
    public LocalDate getFechaReal()         { return fechaReal; }
    public void setFechaReal(LocalDate f)   { this.fechaReal = f; }

    @Override public String toString() {
        if (!laborable) return getNombreCompleto() + " – No laborable";
        return getNombreCompleto() + " – " + bloques + " (" + getTotalMinutosLaborables() + " min)";
    }
}