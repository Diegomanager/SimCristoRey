package com.supermercado.domain.supermercado.service;

import com.supermercado.domain.supermercado.model.Caja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiagnosticoServiceTest {

    private DiagnosticoService service;
    private List<Caja> cajas;

    @BeforeEach
    void setUp() {
        service = new DiagnosticoService();
        cajas = Arrays.asList(new Caja(1, false), new Caja(2, true));
    }

    @Test
    void testDiagnosticarRetornaDiagnosticos() {
        List<DiagnosticoService.Diagnostico> diagnosticos = service.diagnosticar(cajas);
        assertNotNull(diagnosticos);
        assertEquals(2, diagnosticos.size());
    }

    @Test
    void testDiagnosticoCajaNormalSinCola() {
        List<DiagnosticoService.Diagnostico> diagnosticos = service.diagnosticar(cajas);
        DiagnosticoService.Diagnostico d = diagnosticos.get(0);
        assertEquals("CAJA 1", d.getCajaId());
        assertEquals(DiagnosticoService.NivelAlerta.NORMAL, d.getNivel());
        assertEquals("Funcionando correctamente", d.getMensaje());
        assertNotNull(d.getSugerencia());
    }

    @Test
    void testDiagnosticoCajaConColaAlta() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 30; i++) {
            caja.agregarCliente(new com.supermercado.domain.supermercado.model.Cliente(i, 1));
        }
        List<DiagnosticoService.Diagnostico> diagnosticos = service.diagnosticar(Arrays.asList(caja));
        DiagnosticoService.Diagnostico d = diagnosticos.get(0);
        assertEquals(DiagnosticoService.NivelAlerta.ALERTA, d.getNivel());
    }

    @Test
    void testDiagnosticoCajaConColaCritica() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 50; i++) {
            caja.agregarCliente(new com.supermercado.domain.supermercado.model.Cliente(i, 1));
        }
        List<DiagnosticoService.Diagnostico> diagnosticos = service.diagnosticar(Arrays.asList(caja));
        DiagnosticoService.Diagnostico d = diagnosticos.get(0);
        assertEquals(DiagnosticoService.NivelAlerta.CRITICO, d.getNivel());
    }

    @Test
    void testGenerarRecomendacionGeneral() {
        List<DiagnosticoService.Diagnostico> diagnosticos = service.diagnosticar(cajas);
        com.supermercado.domain.supermercado.model.Recomendacion rec = service.generarRecomendacionGeneral(diagnosticos);
        assertNotNull(rec);
        assertNotNull(rec.getMensaje());
        assertNotNull(rec.getPrioridad());
    }
}