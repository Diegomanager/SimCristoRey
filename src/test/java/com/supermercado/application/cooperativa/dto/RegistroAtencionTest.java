package com.supermercado.application.cooperativa.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class RegistroAtencionTest {

    @Test
    void testConstructorYGetters() {
        LocalDate fecha = LocalDate.of(2026, 3, 2);
        LocalTime hora = LocalTime.of(8, 30);
        RegistroAtencion r = new RegistroAtencion(fecha, hora, 5, "C", true, 1000.0);

        assertEquals(fecha, r.getFecha());
        assertEquals(hora, r.getHoraLlegada());
        assertEquals(5, r.getDuracionMinutos());
        assertEquals("C", r.getCodigoServicio());
        assertTrue(r.isEsPreferencial());
        assertEquals(1000.0, r.getMonto());
        assertEquals(8 * 60 + 30, r.getMinutoLlegada());
    }
}