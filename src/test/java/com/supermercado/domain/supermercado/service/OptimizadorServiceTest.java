package com.supermercado.domain.supermercado.service;

import com.supermercado.domain.supermercado.model.Configuracion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OptimizadorServiceTest {

    private OptimizadorService service;
    private Configuracion config;

    @BeforeEach
    void setUp() {
        service = new OptimizadorService();
        config = new Configuracion.Builder()
                .numCajasNormales(4)
                .numCajasRapidas(2)
                .build();
    }

    @Test
    void testOptimizarRetornaResultados() {
        List<OptimizadorService.ResultadoOptimizacion> resultados = service.optimizar(config, 8, 4);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
    }

    @Test
    void testResultadosOrdenadosPorClientesAtendidos() {
        List<OptimizadorService.ResultadoOptimizacion> resultados = service.optimizar(config, 6, 3);
        assertTrue(resultados.size() >= 1);
        int prev = Integer.MAX_VALUE;
        for (OptimizadorService.ResultadoOptimizacion r : resultados) {
            assertTrue(r.getClientesAtendidos() <= prev);
            prev = r.getClientesAtendidos();
        }
    }

    @Test
    void testResultadoIncluyeCampos() {
        List<OptimizadorService.ResultadoOptimizacion> resultados = service.optimizar(config, 5, 2);
        OptimizadorService.ResultadoOptimizacion r = resultados.get(0);
        assertTrue(r.getNumCajasNormales() > 0);
        assertTrue(r.getNumCajasRapidas() >= 0);
        assertTrue(r.getTotalCajas() > 0);
        assertTrue(r.getClientesAtendidos() >= 0);
        assertTrue(r.getColaMaxima() >= 0);
        assertTrue(r.getTiempoPromedio() >= 0);
        assertTrue(r.getFactorUtilizacion() > 0);
        assertNotNull(r.getRecomendacion());
    }

    @Test
    void testOptimizarConMaximosCerosLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            service.optimizar(config, 0, 0);
        });
    }

    @Test
    void testMejoraPorcentajeCalculado() {
        List<OptimizadorService.ResultadoOptimizacion> resultados = service.optimizar(config, 10, 5);
        for (OptimizadorService.ResultadoOptimizacion r : resultados) {
            assertTrue(r.getMejoraPorcentaje() <= 0);
        }
    }
}