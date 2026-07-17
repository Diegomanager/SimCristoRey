package com.supermercado.application.cooperativa.port;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;

import java.io.File;
import java.io.IOException;

public interface IHistorialImportador {
    CalibracionMensual importar(File archivo) throws IOException;
}