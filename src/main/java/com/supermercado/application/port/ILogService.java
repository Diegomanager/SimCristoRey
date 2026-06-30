package com.supermercado.application.port;

/**
 * Puerto de salida: Servicio de logging.
 * Abstrae el mecanismo de registro para mantener
 * la capa de dominio independiente de la infraestructura.
 */
public interface ILogService {
    void info(String mensaje);
    void debug(String mensaje);
    void warn(String mensaje);
    void error(String mensaje);
    void error(String mensaje, Throwable throwable);
}