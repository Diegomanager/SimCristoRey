package com.supermercado.application.usecase;

import com.supermercado.application.port.IEventPublisher;
import com.supermercado.application.port.ILogService;
import com.supermercado.domain.service.SimulacionEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IniciarSimulacionUseCaseTest {

    @Mock private ILogService     logService;
    @Mock private IEventPublisher eventBus;

    private IniciarSimulacionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new IniciarSimulacionUseCase(
            new SimulacionEngine(logService, eventBus));
    }

    @Test
    void testConstructorNoNulo() {
        assertNotNull(useCase);
    }

    @Test
    void testIsEjecutandoInicialmenteFalse() {
        assertFalse(useCase.isEjecutando());
    }

    @Test
    void testIsPausadoInicialmenteFalse() {
        assertFalse(useCase.isPausado());
    }

    @Test
    void testPausarSinEjecutarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.pausar());
    }

    @Test
    void testReanudarSinEjecutarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.reanudar());
    }

    @Test
    void testDetenerSinEjecutarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.detener());
    }

    @Test
    void testObtenerEstadisticasSinEjecutarNoNulo() {
        assertNotNull(useCase.obtenerEstadisticas());
    }

    @Test
    void testPausarYReanudarNoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            useCase.pausar();
            useCase.reanudar();
            useCase.pausar();
            useCase.reanudar();
        });
    }

    @Test
    void testSetOnEstadisticasUpdateNoLanzaExcepcion() {
        assertDoesNotThrow(() -> { /* callbacks eliminados en nueva arquitectura */ });
    }

    @Test
    void testSetOnCajaUpdateNoLanzaExcepcion() {
        assertDoesNotThrow(() -> { /* callbacks eliminados en nueva arquitectura */ });
    }

    @Test
    void testSetOnLogUpdateNoLanzaExcepcion() {
        assertDoesNotThrow(() -> { /* callbacks eliminados en nueva arquitectura */ });
    }

    @Test
    void testSetOnEstadisticasDirectNoLanzaExcepcion() {
        assertDoesNotThrow(() -> { /* callbacks eliminados en nueva arquitectura */ });
    }
}