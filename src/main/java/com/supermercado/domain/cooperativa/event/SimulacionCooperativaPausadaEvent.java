package com.supermercado.domain.cooperativa.event;

public class SimulacionCooperativaPausadaEvent {
    private long timestamp;

    public SimulacionCooperativaPausadaEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() { return timestamp; }
}