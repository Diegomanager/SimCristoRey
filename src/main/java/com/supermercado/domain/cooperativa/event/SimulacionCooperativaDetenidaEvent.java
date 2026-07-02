package com.supermercado.domain.cooperativa.event;

public class SimulacionCooperativaDetenidaEvent {
    private long timestamp;

    public SimulacionCooperativaDetenidaEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
}