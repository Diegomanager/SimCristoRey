package com.supermercado.application.cooperativa.port;

import com.supermercado.application.cooperativa.dto.ResultadoCalibracion;

import java.io.File;
import java.io.IOException;

/**
 * Puerto de entrada: importa un historial real de atenciones (Excel) y
 * devuelve una calibracion automatica de servicios y probabilidades.
 */
public interface IHistorialImportador {
    ResultadoCalibracion importar(File archivo) throws IOException;
}