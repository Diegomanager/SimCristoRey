package com.supermercado.domain.cooperativa.event;

public class EventoSimulacion {
    private final TipoEvento tipo;
    private final String     mensaje;
    private final String     horaSimulada; // "Día 1 – 08:30"

    public EventoSimulacion(TipoEvento tipo, String mensaje, String horaSimulada) {
        this.tipo         = tipo;
        this.mensaje      = mensaje;
        this.horaSimulada = horaSimulada;
    }

    /** Sobrecarga sin hora simulada (usa cadena vacía). */
    public EventoSimulacion(TipoEvento tipo, String mensaje) {
        this(tipo, mensaje, "");
    }

    public TipoEvento getTipo()        { return tipo; }
    public String     getMensaje()     { return mensaje; }
    public String     getHoraSimulada(){ return horaSimulada; }

    @Override
    public String toString() {
        String prefijo = horaSimulada.isEmpty() ? "" : "[" + horaSimulada + "] ";
        return prefijo + mensaje;
    }
}