package com.supermercado.domain.cooperativa.model;

/**
 * Resumen de un dia de simulacion.
 * dia           = numero del dia en el calendario (puede ser 2, 17, 44...)
 * numeroDia     = contador de dias laborables simulados (1, 2, 3...)
 */
public class ResumenDiario {
    private int     dia;            // dia del calendario
    private int     numeroDia;      // contador simulado: 1, 2, 3...
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

    public ResumenDiario(int dia, boolean laborable) {
        this.dia       = dia;
        this.laborable = laborable;
        this.numeroDia = dia; // por defecto igual; se sobrescribe desde el mensual
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
}