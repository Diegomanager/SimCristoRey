package com.supermercado.application.usecase;

import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class ObtenerEstadisticasUseCaseTest {

    private ObtenerEstadisticasUseCase useCase;

    @BeforeEach
    void setUp() { useCase = new ObtenerEstadisticasUseCase(); }

    @Test
    void testEstadisticasCajasNulas() {
        EstadisticasDTO stats = useCase.ejecutar(null);
        assertNotNull(stats);
        assertEquals(0, stats.getTotalClientesAtendidos());
    }

    @Test
    void testEstadisticasCajasVacias() {
        EstadisticasDTO stats = useCase.ejecutar(Collections.emptyList());
        assertNotNull(stats);
        assertEquals(0, stats.getTotalClientesAtendidos());
    }

    @Test
    void testEstadisticasConClienteAtendido() {
        Caja caja = new Caja(1, false);
        Cliente c = new Cliente(1, 5);
        c.setTiempoAtencionReal(3);
        caja.agregarCliente(c);
        caja.prepararSiguienteCliente();
        caja.finalizarAtencion();
        EstadisticasDTO stats = useCase.ejecutar(Arrays.asList(caja));
        assertEquals(1, stats.getTotalClientesAtendidos());
        assertEquals(5, stats.getTotalArticulosVendidos());
    }

    @Test
    void testEstadisticasNoNulas() {
        EstadisticasDTO stats = useCase.ejecutar(Arrays.asList(new Caja(1, false)));
        assertNotNull(stats);
        assertNotNull(stats.getCajeroEstrella());
    }

    @Test
    void testClientesEnCola() {
        Caja c1 = new Caja(1, false);
        Caja c2 = new Caja(2, false);
        c1.agregarCliente(new Cliente(1, 5));
        c1.agregarCliente(new Cliente(2, 5));
        c2.agregarCliente(new Cliente(3, 5));
        EstadisticasDTO stats = useCase.ejecutar(Arrays.asList(c1, c2));
        assertEquals(3, stats.getClientesEnCola());
    }
}