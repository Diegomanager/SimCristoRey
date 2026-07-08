package com.supermercado.domain.cooperativa.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Acumula estadísticas de una fase de simulación (principal o rezagados).
 * Thread-safe para ser actualizado desde el hilo del motor.
 */
public class EstadisticasFase {

    private final String nombre; // "Principal" o "Rezagados"

    private final AtomicInteger totalAtendidos = new AtomicInteger(0);
    private final AtomicLong    sumaEspera     = new AtomicLong(0);   // min
    private final AtomicLong    sumaAtencion   = new AtomicLong(0);   // min
    private final AtomicLong    montoTotalCts  = new AtomicLong(0);   // centavos

    // Por caja
    private final Map<String,Integer> atendidosPorCaja     = new ConcurrentHashMap<>();
    private final Map<String,Long>    tiempoOcupadoPorCaja = new ConcurrentHashMap<>();
    private final Map<String,Double>  montoPorCaja         = new ConcurrentHashMap<>();

    // Por servicio
    private final Map<String,Integer> atendidosPorServicio = new ConcurrentHashMap<>();

    public EstadisticasFase(String nombre) { this.nombre = nombre; }

    public void registrar(com.supermercado.domain.cooperativa.model.Socio socio,
                          com.supermercado.domain.cooperativa.model.Caja  caja) {
        if (socio == null || caja == null) return;

        totalAtendidos.incrementAndGet();
        long espera   = Math.max(0, socio.getTiempoInicioAtencion() - socio.getTiempoLlegada());
        long atencion = Math.max(0, socio.getTiempoSalida() - socio.getTiempoInicioAtencion());
        sumaEspera.addAndGet(espera);
        sumaAtencion.addAndGet(atencion);
        montoTotalCts.addAndGet((long)(socio.getMonto() * 100));

        String id = caja.getId();
        atendidosPorCaja.merge(id, 1, Integer::sum);
        tiempoOcupadoPorCaja.merge(id, atencion, Long::sum);
        montoPorCaja.merge(id, socio.getMonto(), Double::sum);

        for (var s : socio.getTodosLosServicios()) {
            if (s != null) atendidosPorServicio.merge(s.getNombre(), 1, Integer::sum);
        }
    }

    public void reiniciar() {
        totalAtendidos.set(0); sumaEspera.set(0); sumaAtencion.set(0); montoTotalCts.set(0);
        atendidosPorCaja.clear(); tiempoOcupadoPorCaja.clear();
        montoPorCaja.clear(); atendidosPorServicio.clear();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String  getNombre()         { return nombre; }
    public int     getTotalAtendidos() { return totalAtendidos.get(); }
    public double  getMontoTotal()     { return montoTotalCts.get() / 100.0; }
    public double  getPromedioEspera() {
        int t = totalAtendidos.get(); return t == 0 ? 0 : (double) sumaEspera.get() / t;
    }
    public double  getPromedioAtencion() {
        int t = totalAtendidos.get(); return t == 0 ? 0 : (double) sumaAtencion.get() / t;
    }

    public String getCajeroEstrella() {
        return atendidosPorCaja.entrySet().stream()
                .max(Comparator.comparingDouble(e -> eficiencia(e.getKey())))
                .map(Map.Entry::getKey).orElse("N/A");
    }

    public double getEficienciaCajero(String id) { return eficiencia(id); }

    private double eficiencia(String id) {
        int    a = atendidosPorCaja.getOrDefault(id, 0);
        long   t = tiempoOcupadoPorCaja.getOrDefault(id, 1L);
        double m = montoPorCaja.getOrDefault(id, 0.0);
        return t == 0 ? 0 : ((double) a / t) * 1000.0 + m / 10000.0;
    }

    public Map<String,Integer> getAtendidosPorServicio() { return Collections.unmodifiableMap(atendidosPorServicio); }
    public Map<String,Integer> getAtendidosPorCaja()     { return Collections.unmodifiableMap(atendidosPorCaja); }
    public Map<String,Double>  getMontoPorCaja()         { return Collections.unmodifiableMap(montoPorCaja); }
}