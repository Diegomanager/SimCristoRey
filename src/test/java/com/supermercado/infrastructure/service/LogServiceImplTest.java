package com.supermercado.infrastructure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogServiceImplTest {

    private LogServiceImpl logService;

    @BeforeEach
    void setUp() {
        logService = new LogServiceImpl();
    }

    @Test
    void testInfoNoLanzaExcepcion() {
        assertDoesNotThrow(() -> logService.info("mensaje de info"));
    }

    @Test
    void testDebugNoLanzaExcepcion() {
        assertDoesNotThrow(() -> logService.debug("mensaje de debug"));
    }

    @Test
    void testWarnNoLanzaExcepcion() {
        assertDoesNotThrow(() -> logService.warn("mensaje de advertencia"));
    }

    @Test
    void testErrorNoLanzaExcepcion() {
        assertDoesNotThrow(() -> logService.error("mensaje de error"));
    }

    @Test
    void testErrorConThrowableNoLanzaExcepcion() {
        assertDoesNotThrow(() ->
            logService.error("error con causa", new RuntimeException("causa")));
    }

    @Test
    void testMensajeNuloNoLanzaExcepcion() {
        assertDoesNotThrow(() -> logService.info(null));
    }

    @Test
    void testInstanciaCreada() {
        assertNotNull(logService);
    }
}