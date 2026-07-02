package com.supermercado.domain.cooperativa.event;

public class FaseRezagadosIniciadaEvent {
    private int sociosRezagados;
    private long timestamp;

    public FaseRezagadosIniciadaEvent(int sociosRezagados) {
        this.sociosRezagados = sociosRezagados;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSociosRezagados() { return sociosRezagados; }
    public long getTimestamp() { return timestamp; }
}