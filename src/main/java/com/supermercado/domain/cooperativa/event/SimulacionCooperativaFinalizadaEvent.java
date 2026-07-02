package com.supermercado.domain.cooperativa.event;

public class SimulacionCooperativaFinalizadaEvent {
    private long timestamp;

    public SimulacionCooperativaFinalizadaEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
}