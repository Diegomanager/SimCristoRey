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
public class PausarReanudarUseCaseTest {

    private final ILogService     logService  = new LogServiceImpl();
    private final IEventPublisher eventBus    = mock(IEventPublisher.class);

    private SimulacionEngine nuevoEngine() {
        return new SimulacionEngine(logService, eventBus);
    }

    @Test
    void testPausarEjecutarNoLanzaExcepcion() {
        PausarSimulacionUseCase useCase = new PausarSimulacionUseCase(nuevoEngine());
        assertDoesNotThrow(() -> useCase.ejecutar());
    }

    @Test
    void testPausarInstanciaCreada() {
        assertNotNull(new PausarSimulacionUseCase(nuevoEngine()));
    }

    @Test
    void testReanudarEjecutarNoLanzaExcepcion() {
        ReanudarSimulacionUseCase useCase = new ReanudarSimulacionUseCase(nuevoEngine());
        assertDoesNotThrow(() -> useCase.ejecutar());
    }

    @Test
    void testReanudarInstanciaCreada() {
        assertNotNull(new ReanudarSimulacionUseCase(nuevoEngine()));
    }

    @Test
    void testPausarMultiplesLlamadasNoLanzaExcepcion() {
        PausarSimulacionUseCase useCase = new PausarSimulacionUseCase(nuevoEngine());
        assertDoesNotThrow(() -> {
            useCase.ejecutar();
            useCase.ejecutar();
        });
    }
}