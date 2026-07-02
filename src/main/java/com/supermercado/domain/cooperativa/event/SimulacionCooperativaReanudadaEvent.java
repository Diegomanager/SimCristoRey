package com.supermercado.domain.cooperativa.event;

public class SimulacionCooperativaReanudadaEvent {
    private long timestamp;

    public SimulacionCooperativaReanudadaEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
}