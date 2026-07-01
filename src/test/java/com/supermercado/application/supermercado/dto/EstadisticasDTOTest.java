package com.supermercado.application.supermercado.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstadisticasDTOTest {

    @Test
    void testConstructorVacio() {
        EstadisticasDTO dto = new EstadisticasDTO();
        assertEquals(0,       dto.getTotalClientesAtendidos());
        assertEquals(0,       dto.getTotalArticulosVendidos());
        assertEquals(0,       dto.getClientesEnCola());
        assertEquals("-",     dto.getCajeroEstrella());
        assertEquals("08:00", dto.getHoraSimulada());
        assertEquals(0.0,     dto.getTiempoPromedioAtencion());
    }

    @Test
    void testBuilderCompleto() {
        EstadisticasDTO dto = new EstadisticasDTO.Builder()
            .totalClientesAtendidos(100).totalArticulosVendidos(500)
            .totalMinutosAtencion(300).clientesEnCola(5)
            .cajeroEstrella("CAJA 3").tiempoPromedioAtencion(3.0)
            .articulosPromedio(5.0).clientesGenerados(120)
            .horaSimulada("10:30").build();
        assertEquals(100,     dto.getTotalClientesAtendidos());
        assertEquals(500,     dto.getTotalArticulosVendidos());
        assertEquals(300,     dto.getTotalMinutosAtencion());
        assertEquals(5,       dto.getClientesEnCola());
        assertEquals("CAJA 3", dto.getCajeroEstrella());
        assertEquals(3.0,     dto.getTiempoPromedioAtencion());
        assertEquals(5.0,     dto.getArticulosPromedio());
        assertEquals(120,     dto.getClientesGenerados());
        assertEquals("10:30", dto.getHoraSimulada());
    }

    @Test
    void testCajeroEstrellaNoNulo() {
        EstadisticasDTO dto = new EstadisticasDTO.Builder()
            .cajeroEstrella(null).build();
        assertEquals("-", dto.getCajeroEstrella());
    }

    @Test
    void testHoraSimuladaNoNula() {
        EstadisticasDTO dto = new EstadisticasDTO.Builder()
            .horaSimulada(null).build();
        assertEquals("08:00", dto.getHoraSimulada());
    }

    @Test
    void testClientesGenerados() {
        EstadisticasDTO dto = new EstadisticasDTO.Builder()
            .clientesGenerados(250).build();
        assertEquals(250, dto.getClientesGenerados());
    }
}