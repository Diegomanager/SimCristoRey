package com.supermercado.application.supermercado.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class DiagnosticoDTOTest {

    private DiagnosticoDTO.DiagnosticoItem crearItem(String cajaId, String nivel) {
        return new DiagnosticoDTO.DiagnosticoItem(
            cajaId, nivel, "!", "Mensaje prueba", "Sugerencia", 5, 10);
    }

    @Test
    void testCreacionConParametros() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 0L);
        assertNotNull(dto);
    }

    @Test
    void testGetCriticos() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 3L, 1L, 0L);
        assertEquals(3L, dto.getCriticos());
    }

    @Test
    void testGetAlertas() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 2L, 0L);
        assertEquals(2L, dto.getAlertas());
    }

    @Test
    void testGetAtencion() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 4L);
        assertEquals(4L, dto.getAtencion());
    }

    @Test
    void testTieneProblemasConCriticos() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 1L, 0L, 0L);
        assertTrue(dto.tieneProblemas());
    }

    @Test
    void testTieneProblemasConAlertas() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 1L, 0L);
        assertTrue(dto.tieneProblemas());
    }

    @Test
    void testNoTieneProblemasNormal() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 0L);
        assertFalse(dto.tieneProblemas());
    }

    @Test
    void testGetEstadoGeneralCritico() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 1L, 0L, 0L);
        assertEquals("CRITICO", dto.getEstadoGeneral());
    }

    @Test
    void testGetEstadoGeneralAlerta() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 1L, 0L);
        assertEquals("ALERTA", dto.getEstadoGeneral());
    }

    @Test
    void testGetEstadoGeneralAtencion() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 1L);
        assertEquals("ATENCION", dto.getEstadoGeneral());
    }

    @Test
    void testGetEstadoGeneralNormal() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 0L);
        assertEquals("NORMAL", dto.getEstadoGeneral());
    }

    @Test
    void testResumenEjecutivoConParam() {
        DiagnosticoDTO dto = new DiagnosticoDTO(
            Collections.emptyList(), 0L, 0L, 0L, "Todo bien");
        assertEquals("Todo bien", dto.getResumenEjecutivo());
    }

    @Test
    void testResumenEjecutivoNuloPorDefecto() {
        DiagnosticoDTO dto = new DiagnosticoDTO(Collections.emptyList(), 0L, 0L, 0L);
        assertNull(dto.getResumenEjecutivo());
    }

    @Test
    void testGetItemsConDatos() {
        java.util.List<DiagnosticoDTO.DiagnosticoItem> items = Arrays.asList(
            crearItem("CAJA 1", "CRITICO"),
            crearItem("CAJA 2", "ALERTA")
        );
        DiagnosticoDTO dto = new DiagnosticoDTO(items, 1L, 1L, 0L);
        assertEquals(2, dto.getItems().size());
    }

    @Test
    void testDiagnosticoItemGetters() {
        DiagnosticoDTO.DiagnosticoItem item =
            new DiagnosticoDTO.DiagnosticoItem(
                "CAJA 1", "CRITICO", "X", "Cola alta", "Agregar caja", 8, 15);
        assertEquals("CAJA 1",       item.getCajaId());
        assertEquals("CRITICO",      item.getNivel());
        assertEquals("X",            item.getIcono());
        assertEquals("Cola alta",    item.getMensaje());
        assertEquals("Agregar caja", item.getSugerencia());
        assertEquals(8,              item.getColaActual());
        assertEquals(15,             item.getColaMaxima());
    }
}