package com.supermercado.application.port;

import com.supermercado.application.dto.EstadisticasDTO;

public interface IReporteExportador {
    void exportar(EstadisticasDTO estadisticas, String rutaArchivo);
}
