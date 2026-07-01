package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenerarClienteUseCaseTest {

    private GenerarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerarClienteUseCase();
    }

    @Test
    void testGenerarClienteRetornaClienteNoNulo() {
        Cliente c = useCase.ejecutar(1, 10);
        assertNotNull(c);
    }

    @Test
    void testClienteGeneradoTieneArticulosEnRango() {
        for (int i = 0; i < 30; i++) {
            Cliente c = useCase.ejecutar(1, 10);
            assertTrue(c.getCantidadArticulos() >= 1,
                "Articulos debe ser >= 1, fue: " + c.getCantidadArticulos());
            assertTrue(c.getCantidadArticulos() <= 10,
                "Articulos debe ser <= 10, fue: " + c.getCantidadArticulos());
        }
    }

    @Test
    void testClienteGeneradoTieneId() {
        Cliente c = useCase.ejecutar(5, 15);
        assertNotNull(c.getId());
        assertFalse(c.getId().isBlank());
    }

    @Test
    void testRangoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(10, 5));
    }

    @Test
    void testMinNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(-1, 5));
    }

    @Test
    void testRangoIgualMinMax() {
        Cliente c = useCase.ejecutar(5, 5);
        assertEquals(5, c.getCantidadArticulos());
    }
}