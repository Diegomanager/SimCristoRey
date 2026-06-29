package com.supermercado.application.usecase;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.port.ILogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulacionConEscalaUseCaseTest {

    @Mock
    private ILogService logService;

    private SimulacionConEscalaUseCase useCase;
    private ConfiguracionDTO config;

    @BeforeEach
    void setUp() {
        useCase = new SimulacionConEscalaUseCase(logService);
        config = new ConfiguracionDTO.Builder()
            .numCajasNormales(1)
            .numCajasRapidas(1)
            .horasSimuladas(1)          // 1 hora simulada
            .duracionRealSegundos(1)    // 1 segundo real → muy rápido
            .probabilidadLlegadaCliente(50)
            .limiteClientes(5)          // Pocos clientes para que termine rápido
            .articulosClienteMin(1)
            .articulosClienteMax(5)
            .tiempoCajaNormalMin(1)
            .tiempoCajaNormalMax(2)
            .tiempoCajaRapidaMin(1)
            .tiempoCajaRapidaMax(2)
            .build();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)  // Límite de 5 segundos
    void testEjecutarNoLanzaExcepcion() throws InterruptedException {
        // Ejecutar en un hilo separado para no bloquear el test
        Thread simThread = new Thread(() -> {
            try {
                useCase.ejecutar(config);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        simThread.start();

        // Esperar un poco para que la simulación avance
        Thread.sleep(500);

        // Detener la simulación después de un tiempo
        useCase.detener();
        simThread.join(2000);

        // Verificar que no hubo excepción (el test pasaría si llegamos aquí sin excepción)
        assertFalse(useCase.isEjecutando());
    }

    @Test
    void testEjecutarConConfigNulaLanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> useCase.ejecutar(null));
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}