package com.supermercado.application.cooperativa.dto;

import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CalibracionMensualTest {

    @Test
    void testConstructorYGetters() {
        List<ServicioFinanciero> servicios = new ArrayList<>();
        Map<String, Double> probs = new HashMap<>();
        List<LocalDate> dias = List.of(LocalDate.now());
        Map<LocalDate, Integer> sociosPorDia = new HashMap<>();
        List<RegistroAtencion> registros = new ArrayList<>();

        CalibracionMensual cal = new CalibracionMensual(
            servicios, probs, 100, 2, true,
            dias, sociosPorDia, 50, 1.5, registros
        );

        assertSame(servicios, cal.getServicios());
        assertSame(probs, cal.getProbabilidades());
        assertEquals(100, cal.getTotalFichasProcesadas());
        assertEquals(2, cal.getFilasIgnoradas());
        assertTrue(cal.isTieneFechas());
        assertSame(dias, cal.getDiasLaborables());
        assertSame(sociosPorDia, cal.getSociosPorDia());
        assertEquals(50, cal.getMaxSociosDia());
        assertEquals(1.5, cal.getIntervaloPromedioLlegada());
        assertSame(registros, cal.getRegistros());
    }
}