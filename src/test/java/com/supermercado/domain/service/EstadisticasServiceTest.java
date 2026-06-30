package com.supermercado.domain.service;

import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticasServiceTest {

    private EstadisticasService service;
    private List<Caja> cajas;

    @BeforeEach
    void setUp() {
        service = new EstadisticasService();
        cajas = new ArrayList<>();
    }

    @Test
    void testCalcularEstadisticas_CajasVacias() {
        EstadisticasDTO stats = service.calcularEstadisticas(cajas);

        assertEquals(0, stats.getTotalClientesAtendidos());
        assertEquals(0, stats.getTotalArticulosVendidos());
        assertEquals(0, stats.getTotalMinutosAtencion());
        assertEquals(0, stats.getClientesEnCola());
        assertEquals(0.0, stats.getArticulosPromedio(), 0.01);
        assertEquals(0.0, stats.getTiempoPromedioAtencion(), 0.01);
    }

    @Test
    void testCalcularEstadisticas_UnaCaja() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 3; i++) {
            Cliente cliente = new Cliente(i, 4);
            caja.agregarCliente(cliente);
            cliente.setTiempoAtencionReal(2);
            caja.prepararSiguienteCliente();
            caja.finalizarAtencion();
        }
        cajas.add(caja);

        EstadisticasDTO stats = service.calcularEstadisticas(cajas);

        assertEquals(3, stats.getTotalClientesAtendidos());
        assertEquals(12, stats.getTotalArticulosVendidos());
        assertEquals(6, stats.getTotalMinutosAtencion());
        assertEquals(0, stats.getClientesEnCola());
        assertEquals(4.0, stats.getArticulosPromedio(), 0.01);
        assertEquals(2.0, stats.getTiempoPromedioAtencion(), 0.01);
    }

    @Test
    void testCalcularEstadisticas_ConClientesEnCola() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 3; i++) {
            caja.agregarCliente(new Cliente(i, 3));
        }
        cajas.add(caja);

        EstadisticasDTO stats = service.calcularEstadisticas(cajas);

        assertEquals(0, stats.getTotalClientesAtendidos());
        assertEquals(3, stats.getClientesEnCola());
        assertEquals(0, stats.getTotalArticulosVendidos());
    }
}