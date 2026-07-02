package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.model.Socio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasFinancierasService {

    public Map<String, Double> calcularMontosPorServicio(List<Caja> cajas) {
        Map<String, Double> montosPorServicio = new HashMap<>();
        
        for (Caja caja : cajas) {
            for (Socio socio : caja.getSociosAtendidos()) {
                String servicioId = socio.getServicio().getId();
                montosPorServicio.merge(servicioId, socio.getMonto(), Double::sum);
            }
        }
        return montosPorServicio;
    }

    public Map<String, Integer> calcularCantidadPorServicio(List<Caja> cajas) {
        Map<String, Integer> cantidadPorServicio = new HashMap<>();
        
        for (Caja caja : cajas) {
            for (Socio socio : caja.getSociosAtendidos()) {
                String servicioId = socio.getServicio().getId();
                cantidadPorServicio.merge(servicioId, 1, Integer::sum);
            }
        }
        return cantidadPorServicio;
    }

    public double calcularTotalMontos(List<Caja> cajas) {
        return cajas.stream()
                .mapToDouble(Caja::getMontoTotalAtendido)
                .sum();
    }

    public int calcularTotalAtendidos(List<Caja> cajas) {
        return cajas.stream()
                .mapToInt(Caja::getTotalAtendidos)
                .sum();
    }

    public double calcularTiempoPromedioAtencion(List<Caja> cajas) {
        List<Socio> todosAtendidos = cajas.stream()
                .flatMap(c -> c.getSociosAtendidos().stream())
                .toList();
        
        if (todosAtendidos.isEmpty()) {
            return 0.0;
        }
        
        long totalTiempo = todosAtendidos.stream()
                .mapToLong(s -> s.getTiempoSalida() - s.getTiempoInicioAtencion())
                .sum();
        
        return (double) totalTiempo / todosAtendidos.size() / 1000.0; // en segundos
    }

    public double calcularTiempoPromedioEspera(List<Caja> cajas) {
        List<Socio> todosAtendidos = cajas.stream()
                .flatMap(c -> c.getSociosAtendidos().stream())
                .toList();
        
        if (todosAtendidos.isEmpty()) {
            return 0.0;
        }
        
        long totalEspera = todosAtendidos.stream()
                .mapToLong(s -> s.getTiempoInicioAtencion() - s.getTiempoLlegada())
                .sum();
        
        return (double) totalEspera / todosAtendidos.size() / 1000.0; // en segundos
    }

    public Map<String, Double> calcularInteresesGenerados(List<Caja> cajas) {
        Map<String, Double> interesesPorServicio = new HashMap<>();
        
        for (Caja caja : cajas) {
            for (Socio socio : caja.getSociosAtendidos()) {
                String servicioId = socio.getServicio().getId();
                double tasa = socio.getServicio().getTasaInteres() / 100.0;
                double interes = socio.getMonto() * tasa;
                interesesPorServicio.merge(servicioId, interes, Double::sum);
            }
        }
        return interesesPorServicio;
    }
}