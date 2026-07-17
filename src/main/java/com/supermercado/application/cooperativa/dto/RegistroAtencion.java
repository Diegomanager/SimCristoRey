package com.supermercado.application.cooperativa.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Una ficha individual real del historial, lista para "reproducirse" en el
 * motor de simulacion (modo Calibrado/Replay).
 *  - fecha/horaLlegada: vienen de "fecha_creacion" (cuando llego el socio).
 *  - duracionMinutos: viene de hora_fin_atencion - hora_inicio_atencion
 *    (cuanto tardo realmente la atencion).
 * El motor sigue simulando la cola/espera/asignacion de cajas como siempre;
 * solo la llegada y la duracion son "reales" en vez de generadas.
 */
public class RegistroAtencion {
    private final LocalDate fecha;
    private final LocalTime horaLlegada;
    private final int duracionMinutos;
    private final String codigoServicio;
    private final boolean esPreferencial;
    private final double monto;

    public RegistroAtencion(LocalDate fecha, LocalTime horaLlegada, int duracionMinutos,
                             String codigoServicio, boolean esPreferencial, double monto) {
        this.fecha = fecha;
        this.horaLlegada = horaLlegada;
        this.duracionMinutos = duracionMinutos;
        this.codigoServicio = codigoServicio;
        this.esPreferencial = esPreferencial;
        this.monto = monto;
    }

    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraLlegada() { return horaLlegada; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public String getCodigoServicio() { return codigoServicio; }
    public boolean isEsPreferencial() { return esPreferencial; }
    public double getMonto() { return monto; }

    /** Minuto del dia (0-1439) en que llego el socio, para el reloj del motor. */
    public int getMinutoLlegada() {
        return horaLlegada.getHour() * 60 + horaLlegada.getMinute();
    }
}