package com.supermercado.application.usecase;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.port.IConfiguracionRepositorio;
import com.supermercado.application.port.ILogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurarSimulacionUseCaseTest {

    // Stubs minimos
    private static final ILogService LOG = new ILogService() {
        public void info(String m) {} public void debug(String m) {}
        public void warn(String m) {} public void error(String m) {}
        public void error(String m, Throwable t) {}
    };

    private ConfiguracionDTO guardada;

    private final IConfiguracionRepositorio REPO = new IConfiguracionRepositorio() {
        public ConfiguracionDTO cargar() { return guardada; }
        public void guardar(ConfiguracionDTO c) { guardada = c; }
    };

    private ConfigurarSimulacionUseCase useCase;

    @BeforeEach
    void setUp() {
        guardada = null;
        useCase  = new ConfigurarSimulacionUseCase(LOG, REPO);
    }

    @Test
    void testConfigurarGuardaEnRepositorio() {
        ConfiguracionDTO config = config(4, 2);
        useCase.ejecutar(config);
        assertNotNull(guardada);
        assertEquals(4, guardada.getNumCajasNormales());
    }

    @Test
    void testConfigurarNulaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(null));
    }

    @Test
    void testConfigurarCerosCajasNormalesLanzaExcepcion() {
        // Requiere al menos 1 caja normal
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(0).numCajasRapidas(2)
            .horasSimuladas(8).duracionRealSegundos(20)
            .probabilidadLlegadaCliente(45).limiteClientes(0)
            .articulosClienteMin(1).articulosClienteMax(30)
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(config));
    }

    @Test
    void testConfigurarTiempoNormalMinMayorMaxLanzaExcepcion() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(2).numCajasRapidas(1)
            .horasSimuladas(8).duracionRealSegundos(20)
            .probabilidadLlegadaCliente(45).limiteClientes(0)
            .articulosClienteMin(1).articulosClienteMax(30)
            .tiempoCajaNormalMin(10).tiempoCajaNormalMax(4)  // min > max
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(config));
    }

    @Test
    void testConfigurarArticulosMinMayorMaxLanzaExcepcion() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(2).numCajasRapidas(1)
            .horasSimuladas(8).duracionRealSegundos(20)
            .probabilidadLlegadaCliente(45).limiteClientes(0)
            .articulosClienteMin(50).articulosClienteMax(10)  // min > max
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(config));
    }

    private ConfiguracionDTO config(int normales, int rapidas) {
        return new ConfiguracionDTO.Builder()
            .numCajasNormales(normales).numCajasRapidas(rapidas)
            .horasSimuladas(8).duracionRealSegundos(20)
            .probabilidadLlegadaCliente(45).limiteClientes(0)
            .articulosClienteMin(1).articulosClienteMax(30)
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
    }
}