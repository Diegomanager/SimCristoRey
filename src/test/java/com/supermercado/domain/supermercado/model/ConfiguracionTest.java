package com.supermercado.domain.supermercado.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionTest {

    private Configuracion crearDefault() {
        return new Configuracion.Builder().build();
    }

    @Test
    void testBuilderCreaInstancia() {
        assertNotNull(crearDefault());
    }

    @Test
    void testValoresPorDefectoNormales() {
        Configuracion c = crearDefault();
        assertEquals(6, c.getNumCajasNormales());
        assertEquals(2, c.getNumCajasRapidas());
    }

    @Test
    void testBuilderPersonalizado() {
        Configuracion c = new Configuracion.Builder()
            .numCajasNormales(4).numCajasRapidas(2)
            .horasSimuladas(8).duracionRealSegundos(20)
            .probabilidadLlegadaCliente(45).limiteClientes(0)
            .articulosClienteMin(1).articulosClienteMax(30)
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        assertEquals(4, c.getNumCajasNormales());
        assertEquals(2, c.getNumCajasRapidas());
        assertEquals(8, c.getHorasSimuladas());
    }

    @Test
    void testArticulosRangoCoherente() {
        Configuracion c = crearDefault();
        assertTrue(c.getArticulosClienteMin() <= c.getArticulosClienteMax());
    }

    @Test
    void testTiempoNormalCoherente() {
        Configuracion c = crearDefault();
        assertTrue(c.getTiempoCajaNormalMin() <= c.getTiempoCajaNormalMax());
    }

    @Test
    void testTiempoRapidaCoherente() {
        Configuracion c = crearDefault();
        assertTrue(c.getTiempoCajaRapidaMin() <= c.getTiempoCajaRapidaMax());
    }

    @Test
    void testGetCajasInicialmenteVacia() {
        Configuracion c = crearDefault();
        assertNotNull(c.getCajas());
        assertTrue(c.getCajas().isEmpty());
    }

    @Test
    void testGetClientesPorHora() {
        Configuracion c = crearDefault();
        assertTrue(c.getClientesPorHora() > 0);
    }

    @Test
    void testGetTiempoAtencionPromedio() {
        Configuracion c = crearDefault();
        assertTrue(c.getTiempoAtencionPromedio() > 0);
    }

    @Test
    void testBuilderHorasSimuladas() {
        Configuracion c = new Configuracion.Builder().horasSimuladas(24).build();
        assertEquals(24, c.getHorasSimuladas());
    }
}