package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;

import java.util.List;

public class ObtenerColaMaximaUseCase {
    
    public int ejecutar(List<Caja> cajas) {
        if (cajas == null || cajas.isEmpty()) {
            return 0;
        }
        int max = 0;
        for (Caja caja : cajas) {
            int cola = caja.getColaMaxima();
            if (cola > max) {
                max = cola;
            }
        }
        return max;
    }
}
