package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;
import com.supermercado.application.supermercado.port.IReporteExportador;
import com.supermercado.application.supermercado.port.ILogService;

public class ExportarReporteUseCase {
    
    private final ILogService logService;
    private final IReporteExportador exportador;
    
    public ExportarReporteUseCase(ILogService logService, IReporteExportador exportador) {
        this.logService = logService;
        this.exportador = exportador;
    }
    
    public void ejecutar(EstadisticasDTO estadisticas, String rutaArchivo) {
        if (estadisticas == null) {
            throw new IllegalArgumentException("Las estad?sticas no pueden ser nulas");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vac?a");
        }
        
        exportador.exportar(estadisticas, rutaArchivo);
        logService.info("Reporte exportado correctamente");
    }
}
