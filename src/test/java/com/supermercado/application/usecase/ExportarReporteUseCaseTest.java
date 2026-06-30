package com.supermercado.application.usecase;

import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.application.port.ILogService;
import com.supermercado.application.port.IReporteExportador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportarReporteUseCaseTest {

    @Mock
    private ILogService logService;

    @Mock
    private IReporteExportador exportador;

    private ExportarReporteUseCase useCase;
    private EstadisticasDTO estadisticas;

    @BeforeEach
    void setUp() {
        useCase = new ExportarReporteUseCase(logService, exportador);
        estadisticas = new EstadisticasDTO.Builder()
            .totalClientesAtendidos(10)
            .totalArticulosVendidos(50)
            .totalMinutosAtencion(20)
            .clientesEnCola(2)
            .cajeroEstrella("CAJA 1")
            .tiempoPromedioAtencion(2.0)
            .articulosPromedio(5.0)
            .clientesGenerados(12)
            .horaSimulada("10:30")
            .build();
    }

    @Test
    void testEjecutarExportaCorrectamente() {
        doNothing().when(exportador).exportar(estadisticas, "reporte.xlsx");
        assertDoesNotThrow(() -> useCase.ejecutar(estadisticas, "reporte.xlsx"));
        verify(exportador, times(1)).exportar(estadisticas, "reporte.xlsx");
        verify(logService, times(1)).info(anyString());
    }

    @Test
    void testEjecutarConEstadisticasNulasLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.ejecutar(null, "reporte.xlsx");
        });
    }

    @Test
    void testEjecutarConRutaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.ejecutar(estadisticas, "");
        });
    }

    @Test
    void testInstanciaNoNula() {
        assertNotNull(useCase);
    }
}