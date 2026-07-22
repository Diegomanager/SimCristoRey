package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorCooperativaServiceTest {

    private SimuladorCooperativaService service;

    @BeforeEach
    void setUp() {
        service = new SimuladorCooperativaService();
        List<Caja> cajas = new ArrayList<>();
        TipoCaja tipo = new TipoCaja("GENERAL", "General", "GENERAL");
        cajas.add(new Caja("G-01", tipo));
        service.configurar(1L, 200, 1.0, new ArrayList<>(), cajas, null, null);
    }

    @Test
    void setSociosPredefinidos_conLista_activaModoReplay() {
        Socio socio = new Socio();
        socio.setFicha("C001");
        socio.setTiempoLlegada(510);
        socio.setDuracionEstimada(5);

        List<Socio> socios = new ArrayList<>();
        socios.add(socio);

        service.setSociosPredefinidos(socios);
        assertTrue(service.isModoReplay());
    }

    @Test
    void setSociosPredefinidos_null_desactivaModoReplay() {
        service.setSociosPredefinidos(null);
        assertFalse(service.isModoReplay());
    }

    @Test
    void setSociosPredefinidos_listaVacia_desactivaModoReplay() {
        service.setSociosPredefinidos(new ArrayList<>());
        assertFalse(service.isModoReplay());
    }

    @Test
    void getDiaSimulado_reflejaElUltimoValorSeteado() {
        service.setDiaSimulado(3);
        assertEquals(3, service.getDiaSimulado());
    }

    @Test
    void construirResumenDia_sinAtenciones_generadosYAtendidosEnCero() {
        ResumenDiario r = service.construirResumenDia();
        assertNotNull(r);
        assertEquals(0, r.getAtendidosPrincipal());
        assertEquals(0, r.getAtendidosRezagados());
        assertNotNull(r.getAtendidosPorServicio());
    }
}