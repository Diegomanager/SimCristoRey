package com.supermercado.domain.supermercado.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CajaTest {

    private Caja cajaNormal;
    private Caja cajaRapida;

    @BeforeEach
    void setUp() {
        cajaNormal  = new Caja(1, false);
        cajaRapida  = new Caja(2, true);
    }

    @Test
    void testCreacionCajaNormal() {
        assertEquals("CAJA 1", cajaNormal.getId());
        assertFalse(cajaNormal.esRapida());
        assertEquals(EstadoCaja.LIBRE, cajaNormal.getEstado());
        assertEquals(0, cajaNormal.getTotalAtendidos());
        assertEquals(0, cajaNormal.getClientesEnCola());
    }

    @Test
    void testCreacionCajaRapida() {
        assertTrue(cajaRapida.esRapida());
        assertEquals("CAJA 2", cajaRapida.getId());
    }

    @Test
    void testAgregarClienteYCola() {
        Cliente c1 = new Cliente(1, 5);
        Cliente c2 = new Cliente(2, 10);
        cajaNormal.agregarCliente(c1);
        cajaNormal.agregarCliente(c2);
        assertEquals(2, cajaNormal.getClientesEnCola());
        assertEquals(2, cajaNormal.getColaMaxima());
    }

    @Test
    void testPrepararSiguienteCliente() {
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        Cliente preparado = cajaNormal.prepararSiguienteCliente();
        assertNotNull(preparado);
        assertEquals(EstadoCaja.OCUPADA, cajaNormal.getEstado());
        assertEquals(0, cajaNormal.getClientesEnCola());
    }

    @Test
    void testPrepararClienteCuandoColaVacia() {
        Cliente resultado = cajaNormal.prepararSiguienteCliente();
        assertNull(resultado);
    }

    @Test
    void testFinalizarAtencion() {
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        cajaNormal.prepararSiguienteCliente();
        cajaNormal.finalizarAtencion();
        assertEquals(1, cajaNormal.getTotalAtendidos());
        assertEquals(EstadoCaja.LIBRE, cajaNormal.getEstado());
        assertEquals(1, cajaNormal.getClientesAtendidos().size());
    }

    @Test
    void testFinalizarAtencionConClientesPendientes() {
        Cliente c1 = new Cliente(1, 5);
        Cliente c2 = new Cliente(2, 10);
        cajaNormal.agregarCliente(c1);
        cajaNormal.agregarCliente(c2);
        cajaNormal.prepararSiguienteCliente();
        cajaNormal.finalizarAtencion();
        // Debe continuar con c2 automaticamente
        assertEquals(EstadoCaja.OCUPADA, cajaNormal.getEstado());
        assertEquals(1, cajaNormal.getTotalAtendidos());
    }

    @Test
    void testPausarYReanudar() {
        cajaNormal.pausar();
        assertEquals(EstadoCaja.PAUSADA, cajaNormal.getEstado());
        cajaNormal.reanudar();
        assertEquals(EstadoCaja.LIBRE, cajaNormal.getEstado());
    }

    @Test
    void testPausarCajaOcupada() {
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        cajaNormal.prepararSiguienteCliente();
        cajaNormal.pausar();
        assertEquals(EstadoCaja.PAUSADA, cajaNormal.getEstado());
        cajaNormal.reanudar();
        assertEquals(EstadoCaja.OCUPADA, cajaNormal.getEstado());
    }

    @Test
    void testDetener() {
        cajaNormal.detener();
        assertEquals(EstadoCaja.DETENIDA, cajaNormal.getEstado());
        assertFalse(cajaNormal.estaActiva());
    }

    @Test
    void testNoAgregarClienteDetenida() {
        cajaNormal.detener();
        Cliente c = new Cliente(1, 5);
        boolean resultado = cajaNormal.agregarCliente(c);
        assertFalse(resultado);
        assertEquals(0, cajaNormal.getClientesEnCola());
    }

    @Test
    void testNoPrepararClientePausada() {
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        cajaNormal.pausar();
        Cliente preparado = cajaNormal.prepararSiguienteCliente();
        assertNull(preparado);
    }

    @Test
    void testReiniciar() {
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        cajaNormal.prepararSiguienteCliente();
        cajaNormal.reiniciar();
        assertEquals(EstadoCaja.LIBRE, cajaNormal.getEstado());
        assertEquals(0, cajaNormal.getTotalAtendidos());
        assertEquals(0, cajaNormal.getClientesEnCola());
        assertTrue(cajaNormal.getClientesAtendidos().isEmpty());
    }

    @Test
    void testEstaOcupada() {
        assertFalse(cajaNormal.estaOcupada());
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        cajaNormal.prepararSiguienteCliente();
        assertTrue(cajaNormal.estaOcupada());
    }

    @Test
    void testTieneClientesPendientes() {
        assertFalse(cajaNormal.tieneClientesPendientes());
        Cliente c = new Cliente(1, 5);
        cajaNormal.agregarCliente(c);
        assertTrue(cajaNormal.tieneClientesPendientes());
    }

    @Test
    void testToString() {
        String str = cajaNormal.toString();
        assertTrue(str.contains("CAJA 1"));
        assertTrue(str.contains("LIBRE"));
    }
}