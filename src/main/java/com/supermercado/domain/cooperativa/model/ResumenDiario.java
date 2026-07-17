package com.supermercado.domain.cooperativa.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resumen de un dia de simulacion.
 * dia           = numero del dia en el calendario (puede ser 2, 17, 44...)
 * numeroDia     = contador de dias laborables simulados (1, 2, 3...)
 * fecha         = fecha real del calendario (SOLO en modo Calibrado/Replay;
 *                  null en modo Manual -- los reportes deben usar "Dia N"
 *                  como respaldo cuando fecha es null).
 * atendidosPorServicio = cuantos socios de cada codigo de ticket (C,A,S...)
 *                  se atendieron ese dia (Principal+Rezagados combinados).
 */
public class ResumenDiario {
    private int     dia;
    private int     numeroDia;
    private boolean laborable;
    private int     generados;
    private int     atendidosPrincipal;
    private int     atendidosRezagados;
    private int     noAtendidos;
    private double  montoTotal;
    private double  promedioEspera;
    private double  promedioAtencion;
    private String  cajeroEstrella;
    private double  eficienciaGlobal;
    private LocalDate fecha; // NUEVO: null si no se conoce (modo manual)
    private Map<String, Integer> atendidosPorServicio = new LinkedHashMap<>(); // NUEVO

    public ResumenDiario(int dia, boolean laborable) {
        this.dia       = dia;
        this.laborable = laborable;
        this.numeroDia = dia;
    }

    public int     getDia()                     { return dia; }
    public void    setDia(int d)               { this.dia = d; }
    public int     getNumeroDia()               { return numeroDia; }
    public void    setNumeroDia(int n)          { this.numeroDia = n; }
    public boolean isLaborable()               { return laborable; }
    public void    setLaborable(boolean v)     { this.laborable = v; }
    public int     getGenerados()              { return generados; }
    public void    setGenerados(int v)         { generados = v; }
    public int     getAtendidosPrincipal()     { return atendidosPrincipal; }
    public void    setAtendidosPrincipal(int v){ atendidosPrincipal = v; }
    public int     getAtendidosRezagados()     { return atendidosRezagados; }
    public void    setAtendidosRezagados(int v){ atendidosRezagados = v; }
    public int     getTotalAtendidos()         { return atendidosPrincipal + atendidosRezagados; }
    public int     getNoAtendidos()            { return noAtendidos; }
    public void    setNoAtendidos(int v)       { noAtendidos = v; }
    public double  getMontoTotal()             { return montoTotal; }
    public void    setMontoTotal(double v)     { montoTotal = v; }
    public double  getPromedioEspera()         { return promedioEspera; }
    public void    setPromedioEspera(double v) { promedioEspera = v; }
    public double  getPromedioAtencion()       { return promedioAtencion; }
    public void    setPromedioAtencion(double v){ promedioAtencion = v; }
    public String  getCajeroEstrella()         { return cajeroEstrella; }
    public void    setCajeroEstrella(String v) { cajeroEstrella = v; }
    public double  getEficienciaGlobal()       { return eficienciaGlobal; }
    public void    setEficienciaGlobal(double v){ eficienciaGlobal = v; }

    public LocalDate getFecha()                { return fecha; }
    public void       setFecha(LocalDate f)    { this.fecha = f; }

    public Map<String, Integer> getAtendidosPorServicio() {
        return Collections.unmodifiableMap(atendidosPorServicio);
    }
    public void setAtendidosPorServicio(Map<String, Integer> mapa) {
        this.atendidosPorServicio = mapa != null ? new LinkedHashMap<>(mapa) : new LinkedHashMap<>();
    }
}