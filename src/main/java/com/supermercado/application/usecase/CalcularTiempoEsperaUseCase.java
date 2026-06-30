package com.supermercado.application.usecase;

import com.supermercado.domain.model.Cliente;

public class CalcularTiempoEsperaUseCase {
    
    public long ejecutar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        return cliente.calcularTiempoEspera();
    }
}
