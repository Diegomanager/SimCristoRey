package com.supermercado.domain.cooperativa.event;

public class SimulacionCooperativaIniciadaEvent {
    private long timestamp;

    public SimulacionCooperativaIniciadaEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
}