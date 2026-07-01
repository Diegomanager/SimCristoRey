package com.supermercado.domain.supermercado.service;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;
import com.supermercado.domain.supermercado.model.EstadoCaja;
import java.util.List;

public class SimuladorService {

    private final RelojSimulacionService reloj;

    public SimuladorService(RelojSimulacionService reloj) {
        this.reloj = reloj;
    }

    public void asignarCliente(List<Caja> cajas, Cliente cliente) {
        if (cliente == null) {
            System.err.println("ERROR: Cliente nulo");
            return;
        }

        // Imprimir información del cliente (logs de depuración)
        System.out.println("[ASIGNACION] Cliente " + cliente.getId() +
            (cliente.esRapido() ? " (RAPIDO)" : " (NORMAL)") +
            " - Articulos: " + cliente.getCantidadArticulos());

        Caja mejorCaja = null;
        int minCola = Integer.MAX_VALUE;

        for (Caja caja : cajas) {
            if (caja.getEstado() == EstadoCaja.DETENIDA) {
                System.out.println("  Caja " + caja.getId() + " está DETENIDA, saltando");
                continue;
            }
            int colaSize = caja.getClientesEnCola();
            System.out.println("  Caja " + caja.getId() + " (Rapida: " + caja.esRapida() + ") cola=" + colaSize);
            if (colaSize < minCola) {
                minCola = colaSize;
                mejorCaja = caja;
            }
        }

        if (mejorCaja != null) {
            mejorCaja.agregarCliente(cliente);
            System.out.println("  Cliente " + cliente.getId() + " asignado a caja " + mejorCaja.getId() +
                " (cola ahora " + mejorCaja.getClientesEnCola() + ")");
        } else {
            System.err.println("  Cliente " + cliente.getId() + " PERDIDO: no hay cajas disponibles (todas DETENIDAS)");
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