package com.supermercado.domain.cooperativa.model;

public class TipoCaja {
    private String  id;
    private String  nombre;
    private String  prefijoFicha;
    private double  factorVelocidad = 1.0;
    private int     prioridad       = 3;
    private boolean activo          = true;

    public TipoCaja() {}
    public TipoCaja(String id, String nombre, String prefijo) {
        this.id = id; this.nombre = nombre; this.prefijoFicha = prefijo;
    }
    // Getters y Setters
    public String  getId() { return id; }
    public void    setId(String id) { this.id = id; }
    public String  getNombre() { return nombre; }
    public void    setNombre(String n) { this.nombre = n; }
    public String  getPrefijoFicha() { return prefijoFicha; }
    public void    setPrefijoFicha(String p) { this.prefijoFicha = p; }
    public double  getFactorVelocidad() { return factorVelocidad; }
    public void    setFactorVelocidad(double v) { this.factorVelocidad = v; }
    public int     getPrioridad() { return prioridad; }
    public void    setPrioridad(int p) { this.prioridad = p; }
    public boolean isActivo() { return activo; }
    public void    setActivo(boolean a) { this.activo = a; }
}