package com.supermercado.domain.cooperativa.event;

import com.supermercado.domain.cooperativa.model.Socio;
import com.supermercado.domain.cooperativa.model.Caja;

public class SocioAsignadoEvent {
    private Socio socio;
    private Caja caja;
    private long timestamp;

    public SocioAsignadoEvent(Socio socio, Caja caja) {
        this.socio = socio;
        this.caja = caja;
        this.timestamp = System.currentTimeMillis();
    }

    public Socio getSocio() { return socio; }
    public Caja getCaja() { return caja; }
    public long getTimestamp() { return timestamp; }
}