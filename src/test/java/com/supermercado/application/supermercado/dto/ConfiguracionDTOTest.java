package com.supermercado.application.supermercado.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionDTOTest {

    @Test
    void testBuilderValoresCompletos() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(4).numCajasRapidas(2)
            .horasSimuladas(8).duracionRealSegundos(20)
            .limiteClientes(100).probabilidadLlegadaCliente(45)
            .articulosClienteMin(1).articulosClienteMax(30)
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        assertEquals(4,  config.getNumCajasNormales());
        assertEquals(2,  config.getNumCajasRapidas());
        assertEquals(8,  config.getHorasSimuladas());
        assertEquals(20, config.getDuracionRealSegundos());
        assertEquals(100, config.getLimiteClientes());
        assertEquals(45, config.getProbabilidadLlegadaCliente());
        assertEquals(1,  config.getArticulosClienteMin());
        assertEquals(30, config.getArticulosClienteMax());
        assertEquals(4,  config.getTiempoCajaNormalMin());
        assertEquals(9,  config.getTiempoCajaNormalMax());
        assertEquals(2,  config.getTiempoCajaRapidaMin());
        assertEquals(5,  config.getTiempoCajaRapidaMax());
    }

    @Test
    void testBuilderValoresPorDefecto() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder().build();
        assertTrue(config.getNumCajasNormales() >= 0);
        assertTrue(config.getNumCajasRapidas()  >= 0);
    }

    @Test
    void testTotalCajas() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(4).numCajasRapidas(2).build();
        assertEquals(6, config.getNumCajasNormales() + config.getNumCajasRapidas());
    }

    @Test
    void testHorasMaximo() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .horasSimuladas(24).build();
        assertEquals(24, config.getHorasSimuladas());
    }

    @Test
    void testArticulosRango() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .articulosClienteMin(1).articulosClienteMax(10000).build();
        assertTrue(config.getArticulosClienteMin() <= config.getArticulosClienteMax());
    }
}