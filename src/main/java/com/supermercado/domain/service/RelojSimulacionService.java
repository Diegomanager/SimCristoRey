package com.supermercado.domain.service;

public class RelojSimulacionService {
    
    private long tiempoInicio;
    private long tiempoPausado;
    private boolean corriendo;
    private boolean pausado;
    private long tiempoAcumulado;
    private long tiempoSimuladoAcumulado; // minutos simulados

    public RelojSimulacionService() {
        this.tiempoInicio = 0;
        this.tiempoPausado = 0;
        this.corriendo = false;
        this.pausado = false;
        this.tiempoAcumulado = 0;
        this.tiempoSimuladoAcumulado = 0;
    }

    public void iniciar() {
        this.tiempoInicio = System.currentTimeMillis();
        this.corriendo = true;
        this.pausado = false;
        this.tiempoAcumulado = 0;
        this.tiempoSimuladoAcumulado = 0;
    }

    public void detener() {
        this.corriendo = false;
        this.pausado = false;
    }

    public void pausar() {
        if (corriendo && !pausado) {
            this.pausado = true;
            this.tiempoPausado = System.currentTimeMillis();
        }
    }

    public void reanudar() {
        if (corriendo && pausado) {
            long ahora = System.currentTimeMillis();
            tiempoAcumulado += (ahora - tiempoPausado);
            this.pausado = false;
            this.tiempoPausado = 0;
        }
    }

    public void reiniciar() {
        this.tiempoInicio = 0;
        this.tiempoPausado = 0;
        this.corriendo = false;
        this.pausado = false;
        this.tiempoAcumulado = 0;
        this.tiempoSimuladoAcumulado = 0;
    }

    public long getTiempoActual() {
        if (!corriendo) return 0;
        if (pausado) {
            return tiempoAcumulado;
        }
        return tiempoAcumulado + (System.currentTimeMillis() - tiempoInicio);
    }

    /**
     * Actualiza el tiempo simulado en minutos
     */
    public void setTiempoSimulado(long minutosSimulados) {
        this.tiempoSimuladoAcumulado = minutosSimulados;
    }
    
    public long getTiempoSimulado() {
        return tiempoSimuladoAcumulado;
    }

    public boolean estaCorriendo() { return corriendo; }
    public boolean estaPausado() { return pausado; }
}
