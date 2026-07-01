package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;

public class ReiniciarSimulacionUseCase {
    
    public void ejecutar(Caja caja) {
        if (caja != null) {
            caja.reiniciar();
        }
    }
}
