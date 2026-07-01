package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CompararConfiguracionesUseCaseTest {
    private CompararConfiguracionesUseCase useCase;
    private Configuracion configActual;
    private Configuracion configPropuesta;

    @BeforeEach
    void setUp() {
        useCase = new CompararConfiguracionesUseCase();
        configActual = new Configuracion.Builder()
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
        configPropuesta = new Configuracion.Builder()
            .numCajasNormales(3)
            .numCajasRapidas(2)
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
    void testEjecutarRetornaComparacion() {
        String resultado = useCase.ejecutar(configActual, configPropuesta);
        assertNotNull(resultado);
        assertTrue(resultado.contains("CONFIGURACION ACTUAL"));
        assertTrue(resultado.contains("CONFIGURACION PROPUESTA"));
    }

    @Test
    void testEjecutarConConfiguracionesNulasLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            useCase.ejecutar(null, configPropuesta);
        });
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}