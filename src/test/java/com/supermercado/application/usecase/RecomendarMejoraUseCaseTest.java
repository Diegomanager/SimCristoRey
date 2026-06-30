package com.supermercado.application.usecase;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RecomendarMejoraUseCaseTest {
    private RecomendarMejoraUseCase useCase;
    private List<Caja> cajas;
    private Configuracion config;

    @BeforeEach
    void setUp() {
        useCase = new RecomendarMejoraUseCase();
        cajas = new ArrayList<>();
        cajas.add(new Caja(1, true));
        cajas.add(new Caja(2, false));
        config = new Configuracion.Builder()
            .numCajasNormales(2)
            .numCajasRapidas(1)
            .horasSimuladas(1)
            .duracionRealSegundos(3)
            .probabilidadLlegadaCliente(30)
            .limiteClientes(20)
            .articulosClienteMin(1)
            .articulosClienteMax(10)
            .tiempoCajaNormalMin(1)
            .tiempoCajaNormalMax(2)
            .tiempoCajaRapidaMin(1)
            .tiempoCajaRapidaMax(2)
            .build();
    }

    @Test
    void testEjecutarRetornaRecomendacionDTO() {
        var resultado = useCase.ejecutar(cajas, config);
        assertNotNull(resultado);
        assertNotNull(resultado.getMensaje());
    }

    @Test
    void testEjecutarConCajasVaciasNoLanzaExcepcion() {
        assertDoesNotThrow(() -> useCase.ejecutar(new ArrayList<>(), config));
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}