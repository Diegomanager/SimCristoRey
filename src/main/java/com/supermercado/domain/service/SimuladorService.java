package com.supermercado.domain.service;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import com.supermercado.domain.model.EstadoCaja;
import java.util.List;

public class SimuladorService {

    private final RelojSimulacionService reloj;

    public SimuladorService(RelojSimulacionService reloj) {
        this.reloj = reloj;
    }

    public void asignarCliente(List<Caja> cajas, Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        Caja mejorCaja = null;
        int minCola = Integer.MAX_VALUE;

        for (Caja caja : cajas) {
            if (caja.getEstado() == EstadoCaja.DETENIDA) continue;
            // Cliente rápido solo a cajas rápidas, normal a normales
            boolean tipoCoincide = (cliente.esRapido() && caja.esRapida()) ||
                                   (!cliente.esRapido() && !caja.esRapida());
            if (!tipoCoincide) continue;
            int colaSize = caja.getClientesEnCola();
            if (colaSize < minCola) {
                minCola = colaSize;
                mejorCaja = caja;
            }
        }

        if (mejorCaja != null) {
            mejorCaja.agregarCliente(cliente);
        } else {
            // Cliente perdido por falta de caja adecuada (se registra en log del motor)
            System.err.println("Cliente " + cliente.getId() + " perdido: no hay caja " +
                (cliente.esRapido() ? "rápida" : "normal") + " disponible");
        }
    }

    public int getTotalClientesEnCola(List<Caja> cajas) {
        int total = 0;
        for (Caja caja : cajas) {
            total += caja.getClientesEnCola();
        }
        return total;
    }

    public int getTotalClientesAtendidos(List<Caja> cajas) {
        int total = 0;
        for (Caja caja : cajas) {
            total += caja.getTotalAtendidos();
        }
        return total;
    }
}