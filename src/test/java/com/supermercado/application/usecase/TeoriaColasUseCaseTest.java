package com.supermercado.application.usecase;

import com.supermercado.domain.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TeoriaColasUseCaseTest {

    private TeoriaColasUseCase useCase;
    private Configuracion config;

    @BeforeEach
    void setUp() {
        useCase = new TeoriaColasUseCase();
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
    void testEjecutarRetornaMetricas() {
        String resultado = useCase.ejecutar(config);
        assertNotNull(resultado);
        assertTrue(resultado.contains("METRICAS"));   // Cambio aquí
    }

    @Test
    void testObtenerRecomendacionRetornaRecomendacion() {
        var recomendacion = useCase.obtenerRecomendacion(config);
        assertNotNull(recomendacion);
        assertNotNull(recomendacion.getMensaje());
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}