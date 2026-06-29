package com.supermercado.application.usecase;

import com.supermercado.domain.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class OptimizarCajasUseCaseTest {
    private OptimizarCajasUseCase useCase;
    private Configuracion config;

    @BeforeEach
    void setUp() {
        useCase = new OptimizarCajasUseCase();
        config = new Configuracion.Builder()
            .numCajasNormales(2)
            .numCajasRapidas(1)
            .horasSimuladas(1)
            .duracionRealSegundos(3)
            .probabilidadLlegadaCliente(30)
            .limiteClientes(20)
            .articulosClienteMin(1)
            .articulosClienteMax(10)
            .tiempoCajaNormalMin(1)
            .tiempoCajaNormalMax(2)
            .tiempoCajaRapidaMin(1)
            .tiempoCajaRapidaMax(2)
            .build();
    }

    @Test
    void testEjecutarRetornaOptimizacionDTO() {
        var resultado = useCase.ejecutar(config, 5, 3);
        assertNotNull(resultado);
        assertNotNull(resultado.getResultados());
        assertFalse(resultado.getResultados().isEmpty());
    }

    @Test
    void testEjecutarConMaximosNegativosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.ejecutar(config, -1, 3);
        });
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}