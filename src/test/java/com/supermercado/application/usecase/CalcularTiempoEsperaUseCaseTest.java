package com.supermercado.application.usecase;

import com.supermercado.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalcularTiempoEsperaUseCaseTest {

    private CalcularTiempoEsperaUseCase useCase;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        useCase = new CalcularTiempoEsperaUseCase();
        cliente = new Cliente(1, 5);
        cliente.setTiempoLlegada(100);
        cliente.setTiempoInicioAtencion(150);   // Cambio aquí
    }

    @Test
    void testEjecutarConClienteValidoRetornaTiempo() {
        long tiempo = useCase.ejecutar(cliente);
        assertEquals(50, tiempo);
    }

    @Test
    void testEjecutarConClienteNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.ejecutar(null);
        });
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}