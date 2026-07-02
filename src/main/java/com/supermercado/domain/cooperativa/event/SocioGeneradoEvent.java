package com.supermercado.domain.cooperativa.event;

import com.supermercado.domain.cooperativa.model.Socio;

public class SocioGeneradoEvent {
    private Socio socio;
    private long timestamp;

    public SocioGeneradoEvent(Socio socio) {
        this.socio = socio;
        this.timestamp = System.currentTimeMillis();
    }

    public Socio getSocio() { return socio; }
    public long getTimestamp() { return timestamp; }
}