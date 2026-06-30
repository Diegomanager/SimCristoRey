package com.supermercado.application.usecase;

import com.supermercado.application.port.IEventPublisher;
import com.supermercado.application.port.ILogService;
import com.supermercado.domain.service.SimulacionEngine;
import com.supermercado.infrastructure.service.LogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class DetenerSimulacionUseCaseTest {

    private final ILogService     logService = new LogServiceImpl();
    private final IEventPublisher eventBus   = mock(IEventPublisher.class);

    @Test
    void testDetenerInstanciaCreada() {
        assertNotNull(new DetenerSimulacionUseCase(
            new SimulacionEngine(logService, eventBus)));
    }

    @Test
    void testEjecutarSinSimulacionActivaNoLanzaExcepcion() {
        DetenerSimulacionUseCase useCase = new DetenerSimulacionUseCase(
            new SimulacionEngine(logService, eventBus));
        assertDoesNotThrow(() -> useCase.ejecutar());
    }

    @Test
    void testDetenerCambiaEstadoEjecutando() {
        SimulacionEngine engine = new SimulacionEngine(logService, eventBus);
        DetenerSimulacionUseCase useCase = new DetenerSimulacionUseCase(engine);
        useCase.ejecutar();
        assertFalse(engine.isEjecutando());
    }
}