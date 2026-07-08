package com.supermercado.domain.cooperativa.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Caja {
    private String     id;
    private TipoCaja   tipo;
    private EstadoCaja estado = EstadoCaja.LIBRE;
    private Socio      socioActual;
    private boolean    activa = true;
    private int        totalAtendidos = 0;
    private double     montoTotalAtendido = 0.0;
    private long       tiempoTotalOcupado = 0L;
    private long       tiempoInicioAtencion = 0L;
    private final Map<String,Long>    tiempoPorServicio = new LinkedHashMap<>();
    private final Map<String,Integer> atendidosPorServicio = new LinkedHashMap<>();

    public Caja() {}
    public Caja(String id, TipoCaja tipo) { this.id = id; this.tipo = tipo; }

    public void asignarSocio(Socio s, long tiempoActual) {
        this.socioActual = s; this.estado = EstadoCaja.OCUPADA;
        this.tiempoInicioAtencion = tiempoActual;
    }
    public void finalizarAtencion(long tiempoActual) {
        if (socioActual != null) {
            long dur = Math.max(0, tiempoActual - tiempoInicioAtencion);
            tiempoTotalOcupado += dur;
            montoTotalAtendido += socioActual.getMonto();
            totalAtendidos++;
            for (ServicioFinanciero s : socioActual.getTodosLosServicios()) {
                if (s == null) continue;
                tiempoPorServicio.merge(s.getNombre(), dur, Long::sum);
                atendidosPorServicio.merge(s.getNombre(), 1, Integer::sum);
            }
        }
        socioActual = null; estado = EstadoCaja.LIBRE;
    }
    public void pausar()    { if (estado == EstadoCaja.OCUPADA || estado == EstadoCaja.LIBRE) estado = EstadoCaja.PAUSADA; }
    public void reanudar()  { if (estado == EstadoCaja.PAUSADA) estado = EstadoCaja.LIBRE; }
    public void desactivar(){ activa = false; estado = EstadoCaja.DESACTIVADA; }
    public void activar()   { activa = true;  estado = EstadoCaja.LIBRE; }
    public void reiniciar() {
        socioActual = null; estado = EstadoCaja.LIBRE;
        totalAtendidos = 0; montoTotalAtendido = 0.0;
        tiempoTotalOcupado = 0L; tiempoInicioAtencion = 0L;
        tiempoPorServicio.clear(); atendidosPorServicio.clear();
    }
    public long getMinutosEnAtencionActual(long tiempoActual) {
        if (estado != EstadoCaja.OCUPADA || socioActual == null) return 0;
        return Math.max(0, tiempoActual - tiempoInicioAtencion);
    }
    public String getResumenTiempoServicios() {
        if (tiempoPorServicio.isEmpty()) return "Sin atenciones aun";
        StringBuilder sb = new StringBuilder();
        tiempoPorServicio.forEach((svc, mins) -> {
            int cnt = atendidosPorServicio.getOrDefault(svc, 0);
            sb.append(String.format("%-22s %3d vez  %4d min%n", svc, cnt, mins));
        });
        return sb.toString();
    }
    // Getters
    public String     getId() { return id; }
    public void       setId(String id) { this.id = id; }
    public TipoCaja   getTipo() { return tipo; }
    public void       setTipo(TipoCaja t) { this.tipo = t; }
    public EstadoCaja getEstado() { return estado; }
    public void       setEstado(EstadoCaja e) { this.estado = e; }
    public Socio      getSocioActual() { return socioActual; }
    public boolean    isActiva() { return activa; }
    public void       setActiva(boolean v) { this.activa = v; }
    public int        getTotalAtendidos() { return totalAtendidos; }
    public double     getMontoTotalAtendido() { return montoTotalAtendido; }
    public long       getTiempoTotalOcupado() { return tiempoTotalOcupado; }
    public Map<String,Long>    getTiempoPorServicio() { return tiempoPorServicio; }
    public Map<String,Integer> getAtendidosPorServicio() { return atendidosPorServicio; }
}