package com.supermercado.application.usecase;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class ObtenerColaMaximaUseCaseTest {

    private ObtenerColaMaximaUseCase useCase;

    @BeforeEach
    void setUp() { useCase = new ObtenerColaMaximaUseCase(); }

    @Test
    void testColaMaximaCeroSinClientes() {
        assertEquals(0, useCase.ejecutar(Arrays.asList(new Caja(1, false))));
    }

    @Test
    void testColaMaximaListaVacia() {
        assertEquals(0, useCase.ejecutar(Collections.emptyList()));
    }

    @Test
    void testColaMaximaListaNula() {
        assertEquals(0, useCase.ejecutar(null));
    }

    @Test
    void testColaMaximaConClientes() {
        // colaMaxima se actualiza al agregar clientes
        Caja caja = new Caja(1, false);
        caja.agregarCliente(new Cliente(1, 5));
        caja.agregarCliente(new Cliente(2, 5));
        caja.agregarCliente(new Cliente(3, 5));
        // colaMaxima = 3 porque llegaron 3 a la vez
        assertEquals(3, useCase.ejecutar(Arrays.asList(caja)));
    }

    @Test
    void testColaMaximaMultiplesCajas() {
        Caja c1 = new Caja(1, false);
        Caja c2 = new Caja(2, false);
        c1.agregarCliente(new Cliente(1, 5));           // colaMaxima c1 = 1
        c2.agregarCliente(new Cliente(2, 5));
        c2.agregarCliente(new Cliente(3, 5));
        c2.agregarCliente(new Cliente(4, 5));           // colaMaxima c2 = 3
        assertEquals(3, useCase.ejecutar(Arrays.asList(c1, c2)));
    }

    @Test
    void testColaMaximaNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.ejecutar(Arrays.asList(new Caja(1, true))));
    }
}