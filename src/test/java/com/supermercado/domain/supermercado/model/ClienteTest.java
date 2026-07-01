package com.supermercado.domain.supermercado.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    void testCreacionClienteNormal() {
        Cliente c = new Cliente(1, 20);
        assertEquals("Cliente-1", c.getId());   // getId() retorna String "Cliente-N"
        assertEquals(20, c.getCantidadArticulos());
        assertFalse(c.esRapido());
    }

    @Test
    void testCreacionClienteConStringId() {
        Cliente c = new Cliente("CLI-001", 5);
        assertEquals("CLI-001", c.getId());
        assertEquals(5, c.getCantidadArticulos());
    }

    @Test
    void testClienteRapidoPorArticulos() {
        Cliente rapido = new Cliente(1, 8);
        assertTrue(rapido.esRapido());
    }

    @Test
    void testClienteExactamenteDiezArticulos() {
        Cliente c = new Cliente(1, 10);
        assertTrue(c.esRapido());   // <= 10 es rapido
    }

    @Test
    void testClienteNormalPorArticulos() {
        Cliente normal = new Cliente(2, 15);
        assertFalse(normal.esRapido());
    }

    @Test
    void testSetTiempoLlegada() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoLlegada(1000L);
        assertEquals(1000L, c.getTiempoLlegada());
    }

    @Test
    void testSetTiempoLlegadaNegativoLanzaExcepcion() {
        Cliente c = new Cliente(1, 5);
        assertThrows(IllegalArgumentException.class, () -> c.setTiempoLlegada(-1L));
    }

    @Test
    void testSetTiempoAtencionReal() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoAtencionReal(7);
        assertEquals(7, c.getTiempoAtencionReal());
    }

    @Test
    void testSetTiempoAtencionNegativoLanzaExcepcion() {
        Cliente c = new Cliente(1, 5);
        assertThrows(IllegalArgumentException.class, () -> c.setTiempoAtencionReal(-1));
    }

    @Test
    void testSetTiempoSalida() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoSalida(5000L);
        assertEquals(5000L, c.getTiempoSalida());
    }

    @Test
    void testSetTiempoSalidaNegativoLanzaExcepcion() {
        Cliente c = new Cliente(1, 5);
        assertThrows(IllegalArgumentException.class, () -> c.setTiempoSalida(-1L));
    }

    @Test
    void testSetTiempoInicioAtencion() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoInicioAtencion(2000L);
        assertEquals(2000L, c.getTiempoInicioAtencion());
    }

    @Test
    void testCalcularTiempoEspera() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoLlegada(1000L);
        c.setTiempoInicioAtencion(3000L);
        assertEquals(2000L, c.calcularTiempoEspera());
    }

    @Test
    void testCalcularTiempoEsperaSinDatos() {
        Cliente c = new Cliente(1, 5);
        assertEquals(0L, c.calcularTiempoEspera());
    }

    @Test
    void testCalcularTiempoTotal() {
        Cliente c = new Cliente(1, 5);
        c.setTiempoLlegada(1000L);
        c.setTiempoSalida(6000L);
        assertEquals(5000L, c.calcularTiempoTotal());
    }

    @Test
    void testCalcularTiempoTotalSinDatos() {
        Cliente c = new Cliente(1, 5);
        assertEquals(0L, c.calcularTiempoTotal());
    }

    @Test
    void testIdNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(null, 5));
    }

    @Test
    void testArticulosNegativosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("X", -1));
    }

    @Test
    void testEquality() {
        Cliente c1 = new Cliente("CLI-1", 5);
        Cliente c2 = new Cliente("CLI-1", 10);  // mismo id, distintos articulos
        assertEquals(c1, c2);                    // igualdad por id
    }

    @Test
    void testToStringContieneDatos() {
        Cliente c = new Cliente(3, 5);
        String str = c.toString();
        assertTrue(str.contains("Cliente-3"));
    }
}