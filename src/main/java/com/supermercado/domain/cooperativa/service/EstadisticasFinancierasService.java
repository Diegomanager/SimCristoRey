package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.Socio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EstadisticasFinancierasService {

    private final AtomicInteger totalAtendidos        = new AtomicInteger(0);
    private final AtomicLong    sumaEspera            = new AtomicLong(0);
    private final AtomicLong    sumaAtencion          = new AtomicLong(0);
    private final AtomicLong    montoTotal            = new AtomicLong(0);

    private final Map<String, Integer> atendidosPorCaja    = new ConcurrentHashMap<>();
    private final Map<String, Long>    tiempoOcupadoPorCaja = new ConcurrentHashMap<>();
    private final Map<String, Double>  montoPorCaja         = new ConcurrentHashMap<>();

    private final Map<String, Integer> atendidosPorServicio = new ConcurrentHashMap<>();
    private final Map<String, Long>    tiempoPorServicio    = new ConcurrentHashMap<>();

    public void registrarAtencion(Socio socio, Caja caja) {
        if (socio == null || caja == null) return;

        totalAtendidos.incrementAndGet();

        long espera   = socio.getTiempoInicioAtencion() - socio.getTiempoLlegada();
        long atencion = socio.getTiempoSalida() - socio.getTiempoInicioAtencion();
        sumaEspera.addAndGet(Math.max(0, espera));
        sumaAtencion.addAndGet(Math.max(0, atencion));
        montoTotal.addAndGet((long)(socio.getMonto() * 100));

        String cajaId = caja.getId();
        atendidosPorCaja.merge(cajaId, 1, Integer::sum);
        tiempoOcupadoPorCaja.merge(cajaId, Math.max(0, atencion), Long::sum);
        montoPorCaja.merge(cajaId, socio.getMonto(), Double::sum);

        if (socio.getServicio() != null) {
            String svc = socio.getServicio().getNombre();
            atendidosPorServicio.merge(svc, 1, Integer::sum);
            tiempoPorServicio.merge(svc, Math.max(0, atencion), Long::sum);
        }
    }

    public int    getTotalAtendidos()    { return totalAtendidos.get(); }
    public double getMontoTotal()        { return montoTotal.get() / 100.0; }

    public double getPromedioEspera() {
        int t = totalAtendidos.get();
        return t == 0 ? 0.0 : (double) sumaEspera.get() / t;
    }

    public double getPromedioAtencion() {
        int t = totalAtendidos.get();
        return t == 0 ? 0.0 : (double) sumaAtencion.get() / t;
    }

    public String getCajeroEstrella() {
        if (atendidosPorCaja.isEmpty()) return "N/A";
        return atendidosPorCaja.entrySet().stream()
                .max(Comparator.comparingDouble(e -> calcularEficiencia(e.getKey())))
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    public double getEficienciaCajero(String cajaId) {
        return calcularEficiencia(cajaId);
    }

    private double calcularEficiencia(String cajaId) {
        int    atendidos = atendidosPorCaja.getOrDefault(cajaId, 0);
        long   tiempo    = tiempoOcupadoPorCaja.getOrDefault(cajaId, 1L);
        double monto     = montoPorCaja.getOrDefault(cajaId, 0.0);
        if (tiempo == 0) return 0;
        return ((double) atendidos / tiempo) * 1000.0 + monto / 10000.0;
    }

    public Map<String, Integer> getAtendidosPorServicio() {
        return Collections.unmodifiableMap(atendidosPorServicio);
    }

    public Map<String, Double> getMontoPorCaja() {
        return Collections.unmodifiableMap(montoPorCaja);
    }

    public double getTiempoPromedioServicio(String nombreServicio) {
        int    cnt = atendidosPorServicio.getOrDefault(nombreServicio, 0);
        long   t   = tiempoPorServicio.getOrDefault(nombreServicio, 0L);
        return cnt == 0 ? 0.0 : (double) t / cnt;
    }

    public void reiniciar() {
        totalAtendidos.set(0);
        sumaEspera.set(0);
        sumaAtencion.set(0);
        montoTotal.set(0);
        atendidosPorCaja.clear();
        tiempoOcupadoPorCaja.clear();
        montoPorCaja.clear();
        atendidosPorServicio.clear();
        tiempoPorServicio.clear();
    }
}