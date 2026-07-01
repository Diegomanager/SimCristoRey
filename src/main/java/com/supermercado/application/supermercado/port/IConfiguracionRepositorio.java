package com.supermercado.application.supermercado.port;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;

/**
 * Puerto de salida: Repositorio de configuracion.
 * Define el contrato para persistir y recuperar
 * la configuracion del simulador.
 */
public interface IConfiguracionRepositorio {
    ConfiguracionDTO cargar();
    void guardar(ConfiguracionDTO configuracion);
}