package com.supermercado.presentation.cooperativa.view;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integracion de UI REALES sobre el SimuladorCooperativaFrame,
 * usando AssertJ-Swing (no Selenium -- Selenium no puede controlar apps
 * Swing, solo navegadores web).
 *
 * Cada test abre una instancia real del Frame (en el Event Dispatch Thread,
 * como en produccion), simula clics de mouse reales sobre los botones, y
 * verifica el estado de los componentes tal como los veria un usuario.
 */
class SimuladorCooperativaFrameUITest {

    private Robot robot;
    private FrameFixture window;

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        SimuladorCooperativaFrame frame = GuiActionRunner.execute(SimuladorCooperativaFrame::new);
        window = new FrameFixture(robot, frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) window.cleanUp();
        if (robot != null) robot.cleanUp();
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void alAbrir_losBotonesQuedanEnElEstadoInicialCorrecto() {
        window.button(JButtonMatcher.withText("Iniciar")).requireEnabled();
        window.button(JButtonMatcher.withText("Pausar")).requireDisabled();
        window.button(JButtonMatcher.withText("Detener")).requireDisabled();
        window.button(JButtonMatcher.withText("Reiniciar")).requireDisabled();
        window.button(JButtonMatcher.withText("Exportar")).requireDisabled();
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void alHacerClicEnIniciar_seDeshabilitaIniciarYSeHabilitaPausarYDetener() throws InterruptedException {
        window.button(JButtonMatcher.withText("Iniciar")).click();

        // Esperar (polling) a que el evento SIMULACION_INICIADA actualice la UI
        long limite = System.currentTimeMillis() + 10_000;
        boolean pausarHabilitado = false;
        while (System.currentTimeMillis() < limite) {
            pausarHabilitado = GuiActionRunner.execute(() ->
                    window.button(JButtonMatcher.withText("Pausar")).target().isEnabled());
            if (pausarHabilitado) break;
            Thread.sleep(100L);
        }

        assertThat(pausarHabilitado).as("El boton Pausar deberia habilitarse al iniciar").isTrue();
        window.button(JButtonMatcher.withText("Iniciar")).requireDisabled();
        window.button(JButtonMatcher.withText("Detener")).requireEnabled();

        // Limpieza: detener la simulacion para no dejar hilos de fondo corriendo
        window.button(JButtonMatcher.withText("Detener")).click();
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void alAbrirConfiguracion_apareceElDialogoYSePuedeCancelarSinCambios() {
        window.button(JButtonMatcher.withText("Config")).click();

        DialogFixture dialogo = window.robot().finder()
                .findAll(c -> c instanceof java.awt.Dialog).stream()
                .findFirst()
                .map(c -> new DialogFixture(robot, (java.awt.Dialog) c))
                .orElseThrow(() -> new AssertionError("No se encontro el dialogo de configuracion"));

        dialogo.requireVisible();
        dialogo.button(JButtonMatcher.withText("Cancelar")).click();

        // El frame principal sigue en su estado inicial (nada se guardo)
        window.button(JButtonMatcher.withText("Iniciar")).requireEnabled();
    }
}