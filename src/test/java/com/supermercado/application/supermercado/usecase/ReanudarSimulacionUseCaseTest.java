package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.port.IEventPublisher;
import com.supermercado.application.supermercado.port.ILogService;
import com.supermercado.domain.supermercado.service.SimulacionEngine;
import com.supermercado.infrastructure.service.LogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ReanudarSimulacionUseCaseTest {

    private final ILogService     logService = new LogServiceImpl();
    private final IEventPublisher eventBus   = mock(IEventPublisher.class);

    @Test
    void testReanudarInstanciaCreada() {
        assertNotNull(new ReanudarSimulacionUseCase(
            new SimulacionEngine(logService, eventBus)));
    }

    @Test
    void testEjecutarSinSimulacionActivaNoLanzaExcepcion() {
        ReanudarSimulacionUseCase useCase = new ReanudarSimulacionUseCase(
            new SimulacionEngine(logService, eventBus));
        assertDoesNotThrow(() -> useCase.ejecutar());
    }

    @Test
    void testReanudarSinPausaNoLanzaExcepcion() {
        SimulacionEngine engine = new SimulacionEngine(logService, eventBus);
        ReanudarSimulacionUseCase useCase = new ReanudarSimulacionUseCase(engine);
        // Reanudar sin haber pausado no debe lanzar excepcion
        assertDoesNotThrow(() -> useCase.ejecutar());
    }
}