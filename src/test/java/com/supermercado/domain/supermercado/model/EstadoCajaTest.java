package com.supermercado.domain.supermercado.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoCajaTest {

    @Test
    void testValoresEnum() {
        EstadoCaja[] estados = EstadoCaja.values();
        assertEquals(4, estados.length);
        assertEquals(EstadoCaja.LIBRE, estados[0]);
        assertEquals(EstadoCaja.OCUPADA, estados[1]);
        assertEquals(EstadoCaja.PAUSADA, estados[2]);
        assertEquals(EstadoCaja.DETENIDA, estados[3]);
    }

    @Test
    void testEstadoLibre() {
        assertEquals("LIBRE", EstadoCaja.LIBRE.name());
    }

    @Test
    void testEstadoOcupada() {
        assertEquals("OCUPADA", EstadoCaja.OCUPADA.name());
    }

    @Test
    void testEstadoPausada() {
        assertEquals("PAUSADA", EstadoCaja.PAUSADA.name());
    }

    @Test
    void testEstadoDetenida() {
        assertEquals("DETENIDA", EstadoCaja.DETENIDA.name());
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoCaja.LIBRE, EstadoCaja.valueOf("LIBRE"));
        assertEquals(EstadoCaja.OCUPADA, EstadoCaja.valueOf("OCUPADA"));
        assertEquals(EstadoCaja.PAUSADA, EstadoCaja.valueOf("PAUSADA"));
        assertEquals(EstadoCaja.DETENIDA, EstadoCaja.valueOf("DETENIDA"));
    }
}