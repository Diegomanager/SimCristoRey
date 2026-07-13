package com.supermercado.application.cooperativa.usecase;

import com.supermercado.application.cooperativa.port.IReporteExportador;
import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;
import java.io.IOException;
import java.util.List;

public class ExportarReporteUseCase {

    private final IReporteExportador exportador;

    public ExportarReporteUseCase(IReporteExportador exportador) {
        this.exportador = exportador;
    }

    public void ejecutar(String rutaArchivo,
                         List<ResumenDiario> resumenes,
                         EstadisticasFinancierasService estadisticas,
                         long minutosSimulados) throws IOException {
        exportador.exportar(rutaArchivo, resumenes, estadisticas, minutosSimulados);
    }
}