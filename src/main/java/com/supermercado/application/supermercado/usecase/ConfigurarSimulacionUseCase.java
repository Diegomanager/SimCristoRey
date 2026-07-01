package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import com.supermercado.application.supermercado.port.IConfiguracionRepositorio;
import com.supermercado.application.supermercado.port.ILogService;

public class ConfigurarSimulacionUseCase {
    
    private final ILogService logService;
    private final IConfiguracionRepositorio repositorio;
    
    public ConfigurarSimulacionUseCase(ILogService logService, IConfiguracionRepositorio repositorio) {
        this.logService = logService;
        this.repositorio = repositorio;
    }
    
    public void ejecutar(ConfiguracionDTO config) {
        if (config == null) {
            throw new IllegalArgumentException("La configuraci?n no puede ser nula");
        }
        
        validarConfiguracion(config);
        repositorio.guardar(config);
        logService.info("Configuraci?n guardada exitosamente");
    }
    
    private void validarConfiguracion(ConfiguracionDTO config) {
        if (config.getNumCajasNormales() < 1) {
            throw new IllegalArgumentException("Debe haber al menos 1 caja normal");
        }
        if (config.getTiempoCajaNormalMin() > config.getTiempoCajaNormalMax()) {
            throw new IllegalArgumentException("El tiempo m?nimo no puede ser mayor al m?ximo");
        }
        if (config.getTiempoCajaRapidaMin() > config.getTiempoCajaRapidaMax()) {
            throw new IllegalArgumentException("El tiempo m?nimo r?pido no puede ser mayor al m?ximo r?pido");
        }
        if (config.getArticulosClienteMin() > config.getArticulosClienteMax()) {
            throw new IllegalArgumentException("El m?nimo de art?culos no puede ser mayor al m?ximo");
        }
        logService.debug("Configuraci?n validada correctamente");
    }
}
