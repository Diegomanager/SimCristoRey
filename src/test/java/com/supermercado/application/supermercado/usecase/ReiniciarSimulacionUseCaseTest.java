package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ReiniciarSimulacionUseCaseTest {
    private ReiniciarSimulacionUseCase useCase;
    private Caja caja;

    @BeforeEach
    void setUp() {
        useCase = new ReiniciarSimulacionUseCase();
        caja = new Caja(1, true);
    }

    @Test
    void testEjecutarConCajaValidaNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.ejecutar(caja));
    }

    @Test
    void testEjecutarConCajaNulaNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.ejecutar(null));
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}