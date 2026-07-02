package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.EstadoCaja;
import com.supermercado.domain.cooperativa.model.Socio;
import com.supermercado.domain.cooperativa.model.TipoCaja;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Asigna socios a cajas con balanceo de carga equitativo.
 * Estrategia:
 *   1. Busca caja especializada LIBRE del tipo que corresponde al servicio.
 *   2. Si no hay, busca cualquier caja GENERAL LIBRE con menos atendidos.
 *   3. Si tampoco hay, el socio queda en la SalaEspera.
 */
public class AsignadorCajasService {

    public Optional<Caja> asignar(Socio socio, List<Caja> cajas) {
        if (socio == null || cajas == null) return Optional.empty();

        String tipoRequerido = socio.getServicio() != null
                ? socio.getServicio().getTipoCajaRequerido()
                : "GENERAL";

        if (tipoRequerido != null && !tipoRequerido.equals("GENERAL")) {
            Optional<Caja> especializada = cajas.stream()
                    .filter(c -> c.isActiva()
                            && c.getEstado() == EstadoCaja.LIBRE
                            && c.getTipo() != null
                            && c.getTipo().getId().equals(tipoRequerido))
                    .min(Comparator.comparingInt(Caja::getTotalAtendidos));
            if (especializada.isPresent()) return especializada;
        }

        Optional<Caja> general = cajas.stream()
                .filter(c -> c.isActiva()
                        && c.getEstado() == EstadoCaja.LIBRE
                        && c.getTipo() != null
                        && c.getTipo().getId().equals("GENERAL"))
                .min(Comparator.comparingInt(Caja::getTotalAtendidos));

        return general;
    }

    public boolean confirmarAsignacion(Socio socio, Caja caja, long tiempoActual) {
        if (socio == null || caja == null) return false;
        if (caja.getEstado() != EstadoCaja.LIBRE) return false;

        caja.asignarSocio(socio, tiempoActual);
        socio.setTiempoInicioAtencion(tiempoActual);
        return true;
    }

    public List<Caja> cajasDisponibles(List<Caja> cajas) {
        return cajas.stream()
                .filter(c -> c.isActiva() && c.getEstado() == EstadoCaja.LIBRE)
                .sorted(Comparator.comparing((Caja c) -> c.getTipo().getId())
                        .thenComparingInt(Caja::getTotalAtendidos))
                .collect(Collectors.toList());
    }
}