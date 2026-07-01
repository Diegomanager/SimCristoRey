package com.supermercado.domain.supermercado.service;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;
import com.supermercado.application.supermercado.dto.EstadisticasDTO;

import java.util.List;

/**
 * CAJERO ESTRELLA - Metrica IPM (Items Per Minute) diferenciada por tipo:
 *
 * Caja RAPIDA  -> valoramos velocidad de turno (clientes atendidos)
 *                 score = ratioClientes * 0.65 + ipmNorm * 0.35
 *
 * Caja NORMAL  -> valoramos volumen de escaneo (articulos por minuto)
 *                 score = ipmNorm * 0.70 + ratioClientes * 0.30
 *
 * Ambos scores se normalizan sobre promedios globales para
 * que la comparacion sea justa entre tipos distintos de caja.
 */
public class EstadisticasService {

    public EstadisticasDTO calcularEstadisticas(List<Caja> cajas) {
        if (cajas == null || cajas.isEmpty()) {
            return new EstadisticasDTO();
        }

        // --- Pasada 1: totales globales ---
        int totalAtendidos       = 0;
        int totalArticulos       = 0;
        int totalMinutosAtencion = 0;
        int totalEnCola          = 0;

        for (Caja caja : cajas) {
            totalAtendidos += caja.getTotalAtendidos();
            totalEnCola    += caja.getClientesEnCola();
            for (Cliente c : caja.getClientesAtendidos()) {
                totalArticulos       += c.getCantidadArticulos();
                totalMinutosAtencion += c.getTiempoAtencionReal();
            }
        }

        // IPM de referencia global (articulos/minuto promedio de toda la simulacion)
        double ipmGlobal = (totalMinutosAtencion > 0)
            ? (double) totalArticulos / totalMinutosAtencion : 1.0;

        // --- Pasada 2: score IPM por caja ---
        String cajeroEstrella = "-";
        double mejorScore     = -1.0;

        for (Caja caja : cajas) {
            int atendidos = caja.getTotalAtendidos();
            if (atendidos == 0) continue;

            // Articulos y minutos de esta caja especifica
            int artsCaja    = 0;
            int minutosCaja = 0;
            for (Cliente c : caja.getClientesAtendidos()) {
                artsCaja    += c.getCantidadArticulos();
                minutosCaja += c.getTiempoAtencionReal();
            }

            // IPM de esta caja
            double ipm = (minutosCaja > 0)
                ? (double) artsCaja / minutosCaja : 0.0;

            // IPM normalizado: cuanto mejor que el promedio global
            double ipmNorm = (ipmGlobal > 0) ? ipm / ipmGlobal : 0.0;

            // Ratio de clientes: que parte del total atendio esta caja
            double ratioClientes = (totalAtendidos > 0)
                ? (double) atendidos / totalAtendidos : 0.0;

            double score;
            if (caja.esRapida()) {
                // Caja rapida: prioriza clientes atendidos (velocidad de turno)
                score = ratioClientes * 0.65 + ipmNorm * 0.35;
            } else {
                // Caja normal: prioriza articulos por minuto (escaneo)
                score = ipmNorm * 0.70 + ratioClientes * 0.30;
            }

            if (score > mejorScore) {
                mejorScore     = score;
                cajeroEstrella = caja.getId();
            }
        }

        double tiempoPromedio    = (totalAtendidos > 0)
            ? (double) totalMinutosAtencion / totalAtendidos : 0.0;
        double articulosPromedio = (totalAtendidos > 0)
            ? (double) totalArticulos / totalAtendidos : 0.0;

        return new EstadisticasDTO.Builder()
                .totalClientesAtendidos(totalAtendidos)
                .totalArticulosVendidos(totalArticulos)
                .totalMinutosAtencion(totalMinutosAtencion)
                .clientesEnCola(totalEnCola)
                .cajeroEstrella(cajeroEstrella)
                .tiempoPromedioAtencion(tiempoPromedio)
                .articulosPromedio(articulosPromedio)
                .build();
    }
}