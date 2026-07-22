package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticasFinancierasServiceTest {

    private EstadisticasFinancierasService service;

    @BeforeEach
    void setUp() {
        service = new EstadisticasFinancierasService();
    }

    @Test
    void registrarAtencion_acumulaPorCodigo() {
        ServicioFinanciero svc = new ServicioFinanciero();
        svc.setTipoCajaRequerido("C");
        svc.setNombre("Ahorro");

        Socio socio = new Socio();
        socio.setMonto(100.0);
        socio.setTiempoLlegada(0);
        socio.setTiempoInicioAtencion(10);
        socio.setTiempoSalida(15);
        socio.setServicio(svc);

        TipoCaja tipo = new TipoCaja("GENERAL", "General", "GENERAL");
        Caja caja = new Caja("G-01", tipo);

        service.registrarAtencion(socio, caja);

        Map<String, Integer> codigos = service.getPrincipal().getAtendidosPorCodigo();
        assertEquals(1, codigos.get("C"));
    }

    @Test
    void acumularDia_conResumenConDesglose_actualizaAcumulados() {
        ResumenDiario r = new ResumenDiario(1, true);
        Map<String, Integer> desglose = new LinkedHashMap<>();
        desglose.put("C", 5);
        desglose.put("S", 3);
        r.setAtendidosPorServicio(desglose);
        r.setGenerados(8);
        r.setAtendidosPrincipal(8);
        r.setMontoTotal(1000.0);

        service.registrarResumenDiario(r);

        assertEquals(8, service.getAcumGenerados());
        assertEquals(8, service.getAcumAtendPpal());
        assertEquals(1000.0, service.getAcumMonto());
        assertEquals(5, service.getAcumAtendidosPorCodigo().get("C"));
        assertEquals(3, service.getAcumAtendidosPorCodigo().get("S"));
    }

    @Test
    void acumularDia_diaNoLaborable_noAcumulaNada() {
        ResumenDiario r = new ResumenDiario(1, false);
        r.setGenerados(50);
        service.registrarResumenDiario(r);
        assertEquals(0, service.getAcumGenerados());
        assertEquals(0, service.getDiasAcumulados());
    }

    @Test
    void reiniciarCompleto_limpiaAcumulados() {
        ResumenDiario r = new ResumenDiario(1, true);
        r.setGenerados(10);
        r.setAtendidosPrincipal(10);
        service.registrarResumenDiario(r);
        assertEquals(10, service.getAcumGenerados());

        service.reiniciarCompleto();
        assertEquals(0, service.getAcumGenerados());
        assertTrue(service.getAcumAtendidosPorCodigo().isEmpty());
    }
}