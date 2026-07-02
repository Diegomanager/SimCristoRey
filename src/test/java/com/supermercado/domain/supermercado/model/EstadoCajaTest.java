package com.supermercado.domain.supermercado.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadoCajaTest {

    @Test
    void testValoresEnum() {
        EstadoCaja[] valores = EstadoCaja.values();
        assertEquals(4, valores.length, "El enum debe tener 4 valores: LIBRE, OCUPADA, PAUSADA, DETENIDA");
        assertTrue(contains(valores, EstadoCaja.LIBRE));
        assertTrue(contains(valores, EstadoCaja.OCUPADA));
        assertTrue(contains(valores, EstadoCaja.PAUSADA));
        assertTrue(contains(valores, EstadoCaja.DETENIDA));
    }

    @Test
    void testOrdinal() {
        assertEquals(0, EstadoCaja.LIBRE.ordinal());
        assertEquals(1, EstadoCaja.OCUPADA.ordinal());
        assertEquals(2, EstadoCaja.PAUSADA.ordinal());
        assertEquals(3, EstadoCaja.DETENIDA.ordinal());
    }

    private boolean contains(EstadoCaja[] array, EstadoCaja value) {
        for (EstadoCaja e : array) {
            if (e == value) return true;
        }
        return false;
    }
}