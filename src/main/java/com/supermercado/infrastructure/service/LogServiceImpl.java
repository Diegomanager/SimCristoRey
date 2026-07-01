package com.supermercado.infrastructure.service;

import com.supermercado.application.supermercado.port.ILogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador de logging que utiliza SLF4J con Logback.
 * Esta implementación delega todos los mensajes al logger SLF4J,
 * permitiendo configuración externa a través de logback.xml.
 *
 * @author Bazoalto Andia Carlos Diego
 * @version 1.0
 */
public class LogServiceImpl implements ILogService {

    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public void info(String mensaje) {
        if (mensaje != null) {
            logger.info(mensaje);
        }
    }

    @Override
    public void debug(String mensaje) {
        if (mensaje != null) {
            logger.debug(mensaje);
        }
    }

    @Override
    public void warn(String mensaje) {
        if (mensaje != null) {
            logger.warn(mensaje);
        }
    }

    @Override
    public void error(String mensaje) {
        if (mensaje != null) {
            logger.error(mensaje);
        }
    }

    @Override
    public void error(String mensaje, Throwable throwable) {
        if (mensaje != null) {
            logger.error(mensaje, throwable);
        } else if (throwable != null) {
            logger.error("Error sin mensaje", throwable);
        }
    }
}