package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.Socio;
import com.supermercado.domain.cooperativa.model.TipoCaja;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstadisticasFaseTest {

    private EstadisticasFase fase;

    @BeforeEach
    void setUp() {
        fase = new EstadisticasFase("Test");
    }

    @Test
    void testRegistrarAtencionConServicio() {
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

        fase.registrar(socio, caja);

        assertEquals(1, fase.getTotalAtendidos());
        assertEquals(100.0, fase.getMontoTotal());
        assertEquals(10.0, fase.getPromedioEspera());
        assertEquals(5.0, fase.getPromedioAtencion());
        assertEquals(1, fase.getAtendidosPorCodigo().get("C"));
    }
}