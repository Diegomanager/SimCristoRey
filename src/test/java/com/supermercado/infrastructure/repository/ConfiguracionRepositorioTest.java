package com.supermercado.infrastructure.repository;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.port.ILogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionRepositorioTest {

    private ConfiguracionRepositorioImpl repositorio;

    private static final ILogService LOG_STUB = new ILogService() {
        public void info(String m) {}
        public void debug(String m) {}
        public void warn(String m) {}
        public void error(String m) {}
        public void error(String m, Throwable t) {}
    };

    @BeforeEach
    void setUp() {
        repositorio = new ConfiguracionRepositorioImpl(LOG_STUB);
    }

    @Test
    void testCargarRetornaConfiguracionNoNula() {
        assertNotNull(repositorio.cargar());
    }

    @Test
    void testCargarValoresPorDefectoCoherentes() {
        ConfiguracionDTO config = repositorio.cargar();
        assertTrue(config.getNumCajasNormales()            >= 0);
        assertTrue(config.getNumCajasRapidas()             >= 0);
        assertTrue(config.getHorasSimuladas()              >= 1);
        assertTrue(config.getDuracionRealSegundos()        >= 5);
        assertTrue(config.getProbabilidadLlegadaCliente()  >= 1);
        assertTrue(config.getProbabilidadLlegadaCliente() <= 100);
    }

    @Test
    void testGuardarYCargar() {
        ConfiguracionDTO original = new ConfiguracionDTO.Builder()
            .numCajasNormales(6).numCajasRapidas(3)
            .horasSimuladas(10).duracionRealSegundos(30)
            .limiteClientes(0).probabilidadLlegadaCliente(60)
            .articulosClienteMin(1).articulosClienteMax(50)
            .tiempoCajaNormalMin(4).tiempoCajaNormalMax(9)
            .tiempoCajaRapidaMin(2).tiempoCajaRapidaMax(5)
            .build();
        repositorio.guardar(original);
        ConfiguracionDTO cargada = repositorio.cargar();
        assertNotNull(cargada);
        assertEquals(original.getNumCajasNormales(),           cargada.getNumCajasNormales());
        assertEquals(original.getNumCajasRapidas(),            cargada.getNumCajasRapidas());
        assertEquals(original.getHorasSimuladas(),             cargada.getHorasSimuladas());
        assertEquals(original.getProbabilidadLlegadaCliente(), cargada.getProbabilidadLlegadaCliente());
    }

    @Test
    void testGuardarNoLanzaExcepcion() {
        ConfiguracionDTO config = new ConfiguracionDTO.Builder()
            .numCajasNormales(4).numCajasRapidas(2)
            .horasSimuladas(12).duracionRealSegundos(20)
            .build();
        assertDoesNotThrow(() -> repositorio.guardar(config));
    }
}