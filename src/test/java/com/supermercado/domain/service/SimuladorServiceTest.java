package com.supermercado.domain.service;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorServiceTest {

    private SimuladorService service;
    private RelojSimulacionService reloj;
    private List<Caja> cajas;

    @BeforeEach
    void setUp() {
        reloj = new RelojSimulacionService();
        service = new SimuladorService(reloj);
        cajas = new ArrayList<>();
        cajas.add(new Caja(1, true));
        cajas.add(new Caja(2, false));
        cajas.add(new Caja(3, false));
    }

    @Test
    void testAsignarCliente_AsignaCorrectamente() {
        Cliente cliente = new Cliente(1, 5);
        service.asignarCliente(cajas, cliente);

        int totalCola = cajas.stream().mapToInt(Caja::getClientesEnCola).sum();
        assertEquals(1, totalCola);
    }

    @Test
    void testAsignarCliente_MultiplesClientes() {
        for (int i = 1; i <= 6; i++) {
            Cliente cliente = new Cliente(i, 3);
            service.asignarCliente(cajas, cliente);
        }

        int totalCola = cajas.stream().mapToInt(Caja::getClientesEnCola).sum();
        assertEquals(6, totalCola);

        // Distribución balanceada
        for (Caja caja : cajas) {
            assertTrue(caja.getClientesEnCola() >= 1);
            assertTrue(caja.getClientesEnCola() <= 3);
        }
    }

    @Test
    void testGetTotalClientesEnCola() {
        for (int i = 1; i <= 4; i++) {
            Cliente cliente = new Cliente(i, 3);
            service.asignarCliente(cajas, cliente);
        }

        int total = service.getTotalClientesEnCola(cajas);
        assertEquals(4, total);
    }

    @Test
    void testGetTotalClientesAtendidos() {
        Cliente cliente1 = new Cliente(1, 5);
        Cliente cliente2 = new Cliente(2, 3);
        cajas.get(0).agregarCliente(cliente1);
        cajas.get(1).agregarCliente(cliente2);
        cajas.get(0).prepararSiguienteCliente();
        cajas.get(0).finalizarAtencion();

        int atendidos = service.getTotalClientesAtendidos(cajas);
        assertEquals(1, atendidos);
    }

    @Test
    void testAsignarCliente_CajasVacias() {
        List<Caja> cajasVacias = new ArrayList<>();
        Cliente cliente = new Cliente(1, 5);
        service.asignarCliente(cajasVacias, cliente);

        // Sin excepción, cliente no asignado
        assertEquals(0, cajasVacias.size());
    }
}