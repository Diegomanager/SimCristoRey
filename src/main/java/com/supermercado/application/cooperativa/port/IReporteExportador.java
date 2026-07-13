package com.supermercado.application.cooperativa.port;

import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;
import java.io.IOException;
import java.util.List;

public interface IReporteExportador {
    void exportar(String rutaArchivo,
                  List<ResumenDiario> resumenes,
                  EstadisticasFinancierasService estadisticas,
                  long minutosSimulados) throws IOException;
}