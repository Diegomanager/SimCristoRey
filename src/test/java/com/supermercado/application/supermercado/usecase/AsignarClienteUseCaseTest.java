package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AsignarClienteUseCaseTest {

    private AsignarClienteUseCase useCase;
    private List<Caja> cajas;

    @BeforeEach
    void setUp() {
        useCase = new AsignarClienteUseCase();
        cajas = Arrays.asList(new Caja(1, false), new Caja(2, false), new Caja(3, true));
    }

    @Test
    void testAsignarClienteNormalACajaNormal() {
        useCase.ejecutar(cajas, new Cliente(1, 20));
        int totalEnCola = cajas.stream().mapToInt(Caja::getClientesEnCola).sum();
        assertEquals(1, totalEnCola);
    }

    @Test
    void testAsignarClienteRapidoSeAsigna() {
        useCase.ejecutar(cajas, new Cliente(1, 5));
        int totalEnCola = cajas.stream().mapToInt(Caja::getClientesEnCola).sum();
        assertEquals(1, totalEnCola);
    }

    @Test
    void testAsignarVariosClientesDistribuye() {
        for (int i = 1; i <= 6; i++)
            useCase.ejecutar(cajas, new Cliente(i, 15));
        int totalEnCola = cajas.stream().mapToInt(Caja::getClientesEnCola).sum();
        assertEquals(6, totalEnCola);
    }

    @Test
    void testCajasNulasLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> useCase.ejecutar(null, new Cliente(1, 5)));
    }

    @Test
    void testCajasVaciasLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> useCase.ejecutar(Collections.emptyList(), new Cliente(1, 5)));
    }

    @Test
    void testClienteNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> useCase.ejecutar(cajas, null));
    }
}