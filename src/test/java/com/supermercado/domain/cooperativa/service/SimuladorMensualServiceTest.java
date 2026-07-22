package com.supermercado.domain.cooperativa.service;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.dto.RegistroAtencion;
import com.supermercado.domain.cooperativa.config.ConfiguracionCooperativa;
import com.supermercado.domain.cooperativa.event.TipoEvento;
import com.supermercado.domain.cooperativa.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SimuladorMensualServiceTest {

    private SimuladorCooperativaService motor;
    private SimuladorMensualService service;
    private List<JornadaLaboral> jornadas;
    private List<Caja> cajas;
    private ConfiguracionCooperativa config;

    @BeforeEach
    void setUp() {
        motor = new SimuladorCooperativaService();
        cajas = new ArrayList<>();
        cajas.add(new Caja("G-01", new TipoCaja("GENERAL", "General", "GENERAL")));

        config = new ConfiguracionCooperativa();

        JornadaLaboral plantilla = new JornadaLaboral(1, true);
        plantilla.agregarBloque(new BloqueHorario(510, 513)); // jornada de 3 min: test rapido
        jornadas = new ArrayList<>();
        jornadas.add(plantilla);

        // maxSociosDia bajo + 1ms/minuto: cualquier corrida real termina en milisegundos
        motor.configurar(1L, 3, 1.0, new ArrayList<>(), cajas, null, config);
        service = new SimuladorMensualService(motor);
        service.configurar(config, jornadas, cajas, new ArrayList<>(), new ConfiguracionMultiServicio());
        service.setPreguntarRezagadosHabilitado(false); // que no espere confirmacion, drene directo
    }

    private CalibracionMensual calibracionDeUnDia() {
        List<RegistroAtencion> registros = new ArrayList<>();
        registros.add(new RegistroAtencion(LocalDate.of(2026, 3, 2), LocalTime.of(8, 31), 2, "C", false, 100.0));
        List<LocalDate> dias = List.of(LocalDate.of(2026, 3, 2));
        Map<LocalDate, Integer> sociosPorDia = Map.of(LocalDate.of(2026, 3, 2), 1);
        return new CalibracionMensual(new ArrayList<>(), new HashMap<>(), 1, 0, true,
                dias, sociosPorDia, 1, 1.0, registros);
    }

    @Test
    void manual_sinCalibracion_terminaYQuedaSinModoReplay() throws InterruptedException {
        service.setCalibracion(null);

        CountDownLatch latch = new CountDownLatch(1);
        service.addListener(ev -> {
            if (ev.getTipo() == TipoEvento.SIMULACION_MENSUAL_FINALIZADA) latch.countDown();
        });

        service.iniciar();
        boolean termino = latch.await(20, TimeUnit.SECONDS);
        assertTrue(termino, "La simulacion manual deberia terminar");
        assertFalse(service.isModoReplayActivo());
        assertEquals(1, service.getResumenes().size());
        assertNull(service.getResumenes().get(0).getFecha(), "En modo manual la fecha debe quedar null");
    }

    @Test
    void replay_conUnDia_generaResumenConFechaReal() throws InterruptedException {
        service.setCalibracion(calibracionDeUnDia());

        CountDownLatch latch = new CountDownLatch(1);
        service.addListener(ev -> {
            if (ev.getTipo() == TipoEvento.SIMULACION_MENSUAL_FINALIZADA) latch.countDown();
        });

        service.iniciar();
        boolean termino = latch.await(20, TimeUnit.SECONDS);
        assertTrue(termino, "El replay deberia terminar");

        assertTrue(service.isModoReplayActivo());
        assertEquals(1, service.getTotalDiasReplay());
        assertEquals(1, service.getResumenes().size());
        assertEquals(LocalDate.of(2026, 3, 2), service.getResumenes().get(0).getFecha());
        assertEquals(1, service.getResumenes().get(0).getGenerados());
    }

    @Test
    void detener_antesDeIniciar_noLanzaExcepcion() {
        assertDoesNotThrow(() -> service.detener());
    }
}