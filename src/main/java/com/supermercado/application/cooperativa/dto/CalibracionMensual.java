package com.supermercado.application.cooperativa.dto;

import com.supermercado.domain.cooperativa.model.ServicioFinanciero;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CalibracionMensual {

    private final List<ServicioFinanciero> servicios;
    private final Map<String, Double> probabilidades;
    private final int totalFichasProcesadas;
    private final int filasIgnoradas;

    private final boolean tieneFechas;
    private final List<LocalDate> diasLaborables;
    private final Map<LocalDate, Integer> sociosPorDia;
    private final int maxSociosDia;
    private final double intervaloPromedioLlegada;

    private final List<RegistroAtencion> registros;

    public CalibracionMensual(List<ServicioFinanciero> servicios,
                               Map<String, Double> probabilidades,
                               int totalFichasProcesadas,
                               int filasIgnoradas,
                               boolean tieneFechas,
                               List<LocalDate> diasLaborables,
                               Map<LocalDate, Integer> sociosPorDia,
                               int maxSociosDia,
                               double intervaloPromedioLlegada,
                               List<RegistroAtencion> registros) {
        this.servicios = servicios;
        this.probabilidades = probabilidades;
        this.totalFichasProcesadas = totalFichasProcesadas;
        this.filasIgnoradas = filasIgnoradas;
        this.tieneFechas = tieneFechas;
        this.diasLaborables = diasLaborables;
        this.sociosPorDia = sociosPorDia;
        this.maxSociosDia = maxSociosDia;
        this.intervaloPromedioLlegada = intervaloPromedioLlegada;
        this.registros = registros;
    }

    public List<ServicioFinanciero> getServicios() { return servicios; }
    public Map<String, Double> getProbabilidades() { return probabilidades; }
    public int getTotalFichasProcesadas() { return totalFichasProcesadas; }
    public int getFilasIgnoradas() { return filasIgnoradas; }
    public boolean isTieneFechas() { return tieneFechas; }
    public List<LocalDate> getDiasLaborables() { return diasLaborables; }
    public Map<LocalDate, Integer> getSociosPorDia() { return sociosPorDia; }
    public int getMaxSociosDia() { return maxSociosDia; }
    public double getIntervaloPromedioLlegada() { return intervaloPromedioLlegada; }
    public List<RegistroAtencion> getRegistros() { return registros; }
}