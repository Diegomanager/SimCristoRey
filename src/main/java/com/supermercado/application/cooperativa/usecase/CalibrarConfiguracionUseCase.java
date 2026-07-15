package com.supermercado.application.cooperativa.usecase;

import com.supermercado.application.cooperativa.dto.ResultadoCalibracion;
import com.supermercado.application.cooperativa.port.IHistorialImportador;

import java.io.File;
import java.io.IOException;

public class CalibrarConfiguracionUseCase {

    private final IHistorialImportador importador;

    public CalibrarConfiguracionUseCase(IHistorialImportador importador) {
        this.importador = importador;
    }

    public ResultadoCalibracion ejecutar(File archivo) throws IOException {
        return importador.importar(archivo);
    }
}