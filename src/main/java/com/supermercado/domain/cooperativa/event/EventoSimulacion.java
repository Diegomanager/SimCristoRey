package com.supermercado.domain.cooperativa.event;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EventoSimulacion {
    private final TipoEvento tipo;
    private final String     mensaje;
    private final String     hora;

    public EventoSimulacion(TipoEvento tipo, String mensaje) {
        this.tipo    = tipo;
        this.mensaje = mensaje;
        this.hora    = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public TipoEvento getTipo()    { return tipo; }
    public String     getMensaje() { return mensaje; }
    public String     getHora()    { return hora; }

    @Override
    public String toString() {
        return "[" + hora + "] " + tipo.name() + ": " + mensaje;
    }
}