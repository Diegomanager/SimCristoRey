package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.EstadoCaja;
import com.supermercado.domain.cooperativa.model.Socio;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AsignadorCajasService {

    public Optional<Caja> asignar(Socio socio, List<Caja> cajas) {
        if (socio == null || cajas == null || cajas.isEmpty()) return Optional.empty();
        String dest = socio.getTipoCajaDestino() != null ? socio.getTipoCajaDestino() : "GENERAL";

        return switch (dest) {
            case "PLATAFORMA" -> buscarCaja(cajas, "PLATAFORMA");
            case "MM" -> {
                Optional<Caja> mm = buscarCaja(cajas, "MM");
                yield mm.isPresent() ? mm : buscarCaja(cajas, "GENERAL");
            }
            default -> cajas.stream()
                    .filter(c -> c.isActiva() && c.getEstado() == EstadoCaja.LIBRE
                            && (tipoCaja(c).equals("GENERAL") || tipoCaja(c).equals("MM")))
                    .min(Comparator.comparingInt(Caja::getTotalAtendidos));
        };
    }

    private Optional<Caja> buscarCaja(List<Caja> cajas, String tipo) {
        return cajas.stream()
                .filter(c -> c.isActiva() && c.getEstado() == EstadoCaja.LIBRE
                        && tipoCaja(c).equals(tipo))
                .min(Comparator.comparingInt(Caja::getTotalAtendidos));
    }

    private String tipoCaja(Caja c) {
        if (c.getTipo() == null) return "GENERAL";
        return c.getTipo().getId().toUpperCase();
    }

    public boolean confirmarAsignacion(Socio socio, Caja caja, long tiempoActual) {
        if (socio == null || caja == null || caja.getEstado() != EstadoCaja.LIBRE) return false;
        caja.asignarSocio(socio, tiempoActual);
        socio.setTiempoInicioAtencion(tiempoActual);
        return true;
    }

    public List<Caja> cajasDisponibles(List<Caja> cajas) {
        return cajas.stream()
                .filter(c -> c.isActiva() && c.getEstado() == EstadoCaja.LIBRE)
                .sorted(Comparator.comparingInt(Caja::getTotalAtendidos))
                .collect(Collectors.toList());
    }
}