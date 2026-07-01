package com.supermercado.domain.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Define un tipo de caja (ventanilla) con sus características.
 * Ejemplos: Módulo Móvil (MM), Punto de Reclamo (REC), Punto de Ahorro (AHO),
 * Créditos (CRE), General (GEN).
 */
public class TipoCaja {
    private final String id;
    private final String nombre;
    private String prefijoFicha;
    private double factorVelocidad;
    private int prioridad;
    private Set<String> serviciosEspecializados;
    private boolean activo;

    public TipoCaja(String id, String nombre) {
        this.id = Objects.requireNonNull(id, "ID no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        this.prefijoFicha = id.substring(0, Math.min(3, id.length())).toUpperCase();
        this.factorVelocidad = 1.0;
        this.prioridad = 2;
        this.serviciosEspecializados = new HashSet<>();
        this.activo = true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPrefijoFicha() { return prefijoFicha; }
    public void setPrefijoFicha(String prefijoFicha) {
        if (prefijoFicha == null || prefijoFicha.isBlank()) {
            throw new IllegalArgumentException("El prefijo de ficha no puede estar vacío");
        }
        this.prefijoFicha = prefijoFicha.toUpperCase();
    }
    public double getFactorVelocidad() { return factorVelocidad; }
    public void setFactorVelocidad(double factorVelocidad) {
        if (factorVelocidad <= 0) throw new IllegalArgumentException("El factor de velocidad debe ser positivo");
        this.factorVelocidad = factorVelocidad;
    }
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > 3) throw new IllegalArgumentException("La prioridad debe ser 1 (alta), 2 (media) o 3 (baja)");
        this.prioridad = prioridad;
    }
    public Set<String> getServiciosEspecializados() { return Set.copyOf(serviciosEspecializados); }
    public void agregarServicioEspecializado(String servicioId) {
        serviciosEspecializados.add(servicioId);
    }
    public void quitarServicioEspecializado(String servicioId) {
        serviciosEspecializados.remove(servicioId);
    }
    public boolean esEspecializadoEn(String servicioId) {
        return serviciosEspecializados.contains(servicioId);
    }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int calcularTiempoAtencion(int duracionBase, String servicioId) {
        double factor = factorVelocidad;
        if (serviciosEspecializados.contains(servicioId)) {
            factor *= 0.8;
        }
        return (int) Math.ceil(duracionBase * factor);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] prefijo=%s, vel=%.2f", nombre, id, prefijoFicha, factorVelocidad);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoCaja tipoCaja = (TipoCaja) o;
        return Objects.equals(id, tipoCaja.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}
