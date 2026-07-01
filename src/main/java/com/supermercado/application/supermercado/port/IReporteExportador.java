package com.supermercado.application.supermercado.port;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;

public interface IReporteExportador {
    void exportar(EstadisticasDTO estadisticas, String rutaArchivo);
}
