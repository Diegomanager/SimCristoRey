package com.supermercado.application.usecase;

import com.supermercado.domain.model.Caja;

public class ReiniciarSimulacionUseCase {
    
    public void ejecutar(Caja caja) {
        if (caja != null) {
            caja.reiniciar();
        }
    }
}
