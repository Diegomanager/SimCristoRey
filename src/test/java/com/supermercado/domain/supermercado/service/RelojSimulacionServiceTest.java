package com.supermercado.domain.supermercado.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RelojSimulacionServiceTest {

    private RelojSimulacionService reloj;

    @BeforeEach
    void setUp() {
        reloj = new RelojSimulacionService();
    }

    @Test
    void testEstadoInicial() {
        assertFalse(reloj.estaCorriendo());
        assertFalse(reloj.estaPausado());
        assertEquals(0, reloj.getTiempoActual());
        assertEquals(0, reloj.getTiempoSimulado());
    }

    @Test
    void testIniciar() {
        reloj.iniciar();
        assertTrue(reloj.estaCorriendo());
        assertFalse(reloj.estaPausado());
        assertEquals(0, reloj.getTiempoActual());
    }

    @Test
    void testPausar() {
        reloj.iniciar();
        reloj.pausar();
        // Después de pausar, sigue corriendo pero pausado
        assertTrue(reloj.estaCorriendo());
        assertTrue(reloj.estaPausado());
    }

    @Test
    void testReanudar() {
        reloj.iniciar();
        reloj.pausar();
        reloj.reanudar();
        assertTrue(reloj.estaCorriendo());
        assertFalse(reloj.estaPausado());
    }

    @Test
    void testDetener() {
        reloj.iniciar();
        reloj.detener();
        assertFalse(reloj.estaCorriendo());
        assertFalse(reloj.estaPausado());
    }

    @Test
    void testSetTiempoSimulado() {
        reloj.iniciar();
        reloj.setTiempoSimulado(10);
        // Verificar que el tiempo simulado se actualiza
        assertEquals(10, reloj.getTiempoSimulado());
        // getTiempoActual puede ser diferente por la implementación
        assertTrue(reloj.getTiempoActual() >= 0);
    }
}