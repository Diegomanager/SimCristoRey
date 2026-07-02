package com.supermercado.domain.cooperativa.event;

public class EventoSimulacion {
    private final TipoEvento tipo;
    private final String     mensaje;
    private final long       tiempoSimulado; // minutos simulados

    public EventoSimulacion(TipoEvento tipo, String mensaje, long tiempoSimulado) {
        this.tipo    = tipo;
        this.mensaje = mensaje;
        this.tiempoSimulado = tiempoSimulado;
    }

    public EventoSimulacion(TipoEvento tipo, String mensaje) {
        this(tipo, mensaje, 0);
    }

    public TipoEvento getTipo()    { return tipo; }
    public String     getMensaje() { return mensaje; }
    public long       getTiempoSimulado() { return tiempoSimulado; }

    public String getHoraSimulada() {
        if (tiempoSimulado <= 0) return "??:??";
        int horas = (int) (tiempoSimulado / 60);
        int mins  = (int) (tiempoSimulado % 60);
        return String.format("%02d:%02d", horas + 8, mins); // 8:00 de apertura
    }

    @Override
    public String toString() {
        String hora = getHoraSimulada();
        return "[" + hora + "] " + tipo.name() + ": " + mensaje;
    }
}