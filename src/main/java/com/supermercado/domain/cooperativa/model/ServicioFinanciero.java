package com.supermercado.domain.cooperativa.model;

public class ServicioFinanciero {
    private String  id;
    private String  nombre;
    private String  tipoCajaRequerido;
    private int     duracionMinima;
    private int     duracionMaxima;
    private double  montoMinimo;
    private double  montoMaximo;
    private double  tasaInteres;
    private double  probabilidad;
    private boolean activo = true;
    private String  prefijoTicket;
    private int     idTipoServicio;

    public ServicioFinanciero() {}

    // Constructor completo (10 parámetros)
    public ServicioFinanciero(String id, String nombre, String tipoCaja,
                               int durMin, int durMax,
                               double monMin, double monMax,
                               double tasa, double prob,
                               String prefijo, int idTipo) {
        this.id = id; this.nombre = nombre; this.tipoCajaRequerido = tipoCaja;
        this.duracionMinima = durMin; this.duracionMaxima = durMax;
        this.montoMinimo = monMin; this.montoMaximo = monMax;
        this.tasaInteres = tasa; this.probabilidad = prob;
        this.prefijoTicket = prefijo;
        this.idTipoServicio = idTipo;
    }

    // NUEVO: Constructor con 9 parámetros (sin prefijo ni idTipo) para compatibilidad
    // Asigna prefijo = tipoCaja, idTipo = 0
    public ServicioFinanciero(String id, String nombre, String tipoCaja,
                               int durMin, int durMax,
                               double monMin, double monMax,
                               double tasa, double prob) {
        this(id, nombre, tipoCaja, durMin, durMax, monMin, monMax, tasa, prob, tipoCaja, 0);
    }

    // Getters y Setters (todos)
    public String  getId() { return id; }
    public void    setId(String id) { this.id = id; }
    public String  getNombre() { return nombre; }
    public void    setNombre(String n) { this.nombre = n; }
    public String  getTipoCajaRequerido() { return tipoCajaRequerido; }
    public void    setTipoCajaRequerido(String t){ this.tipoCajaRequerido = t; }
    public int     getDuracionMinima() { return duracionMinima; }
    public void    setDuracionMinima(int v) { this.duracionMinima = Math.max(1,v); }
    public int     getDuracionMaxima() { return duracionMaxima; }
    public void    setDuracionMaxima(int v) { this.duracionMaxima = Math.max(1,v); }
    public double  getMontoMinimo() { return montoMinimo; }
    public void    setMontoMinimo(double v) { this.montoMinimo = v; }
    public double  getMontoMaximo() { return montoMaximo; }
    public void    setMontoMaximo(double v) { this.montoMaximo = v; }
    public double  getTasaInteres() { return tasaInteres; }
    public void    setTasaInteres(double v) { this.tasaInteres = v; }
    public double  getProbabilidad() { return probabilidad; }
    public void    setProbabilidad(double v){ this.probabilidad = v; }
    public boolean isActivo() { return activo; }
    public void    setActivo(boolean a) { this.activo = a; }
    public String  getPrefijoTicket() { return prefijoTicket; }
    public void    setPrefijoTicket(String p){ this.prefijoTicket = p; }
    public int     getIdTipoServicio() { return idTipoServicio; }
    public void    setIdTipoServicio(int id){ this.idTipoServicio = id; }

    @Override
    public String toString() {
        return nombre + " [" + prefijoTicket + "] " + duracionMinima + "-" + duracionMaxima + " min";
    }
}