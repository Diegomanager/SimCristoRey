package com.supermercado.regression;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import com.supermercado.application.supermercado.usecase.IniciarSimulacionUseCase;
import com.supermercado.domain.supermercado.service.SimulacionEngine;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;
import com.supermercado.infrastructure.service.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("regression")
class SimulacionRegressionTest {

    private ConfiguracionDTO config;
    private IniciarSimulacionUseCase useCase;

    @BeforeEach
    void setUp() {
        config = new ConfiguracionDTO.Builder()
            .numCajasNormales(2)
            .numCajasRapidas(1)
            .horasSimuladas(1)
            .duracionRealSegundos(8)
            .probabilidadLlegadaCliente(30)
            .limiteClientes(20)
            .articulosClienteMin(1)
            .articulosClienteMax(10)
            .tiempoCajaNormalMin(1)
            .tiempoCajaNormalMax(2)
            .tiempoCajaRapidaMin(1)
            .tiempoCajaRapidaMax(2)
            .build();

        useCase = new IniciarSimulacionUseCase(new SimulacionEngine(new LogServiceImpl(), new EventBusAdapter()));
    }

    @Test
    @Tag("regression")
    void testRegression_InicioSimulacion() {
        assertDoesNotThrow(() -> useCase.ejecutar(config));
    }

    @Test
    void testRegression_PausaYReanudacion() throws InterruptedException {
        Thread simThread = new Thread(() -> {
            try {
                useCase.ejecutar(config);
            } catch (Exception e) {
                // Ignorar
            }
        });
        simThread.start();

        Thread.sleep(2000);
        assertTrue(useCase.isEjecutando(), "La simulación debería estar ejecutando antes de pausar");

        useCase.pausar();
        Thread.sleep(500);
        assertTrue(useCase.isPausado(), "La simulación debería estar pausada");

        useCase.reanudar();
        Thread.sleep(500);
        assertFalse(useCase.isPausado(), "La simulación debería estar reanudada");

        useCase.detener();
        simThread.join(1000);
    }

    @Test
    @Tag("regression")
    void testRegression_DetencionSimulacion() throws InterruptedException {
        Thread simThread = new Thread(() -> {
            try {
                useCase.ejecutar(config);
            } catch (Exception e) {
                // Ignorar
            }
        });
        simThread.start();

        Thread.sleep(2000);
        useCase.detener();
        assertFalse(useCase.isEjecutando());
        simThread.join(1000);
    }

    @Test
    @Tag("regression")
    void testRegression_ConfiguracionValida() {
        assertTrue(config.getNumCajasNormales() > 0);
        assertTrue(config.getNumCajasRapidas() >= 0);
        assertTrue(config.getHorasSimuladas() > 0);
        assertTrue(config.getProbabilidadLlegadaCliente() >= 0 && config.getProbabilidadLlegadaCliente() <= 100);
        assertTrue(config.getArticulosClienteMin() <= config.getArticulosClienteMax());
        assertTrue(config.getTiempoCajaNormalMin() <= config.getTiempoCajaNormalMax());
        assertTrue(config.getTiempoCajaRapidaMin() <= config.getTiempoCajaRapidaMax());
    }

    @Test
    @Tag("regression")
    void testRegression_SimulacionCompleta() throws InterruptedException {
        ConfiguracionDTO configLarga = new ConfiguracionDTO.Builder()
            .numCajasNormales(2)
            .numCajasRapidas(1)
            .horasSimuladas(1)
            .duracionRealSegundos(10)
            .probabilidadLlegadaCliente(30)
            .limiteClientes(10)
            .articulosClienteMin(1)
            .articulosClienteMax(10)
            .tiempoCajaNormalMin(1)
            .tiempoCajaNormalMax(2)
            .tiempoCajaRapidaMin(1)
            .tiempoCajaRapidaMax(2)
            .build();

        IniciarSimulacionUseCase useCaseLarga = new IniciarSimulacionUseCase(new SimulacionEngine(new LogServiceImpl(), new EventBusAdapter()));
        Thread simThread = new Thread(() -> {
            try {
                useCaseLarga.ejecutar(configLarga);
            } catch (Exception e) {
                // Ignorar
            }
        });
        simThread.start();
        simThread.join(25000);
        assertFalse(useCaseLarga.isEjecutando(), "La simulación debería haber terminado");
    }
}