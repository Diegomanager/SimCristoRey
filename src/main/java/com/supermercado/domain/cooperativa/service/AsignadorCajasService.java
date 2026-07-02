package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.SalaEspera;
import com.supermercado.domain.cooperativa.model.Socio;
import com.supermercado.domain.cooperativa.model.EstadoCaja;

import java.util.List;

public class AsignadorCajasService {

    public boolean asignarSocio(SalaEspera salaEspera, List<Caja> cajas) {
        // Verificar que haya socios en espera
        if (salaEspera.isEmpty()) {
            return false;
        }
        
        // Buscar una caja libre y activa
        Caja cajaLibre = cajas.stream()
                .filter(Caja::isActiva)
                .filter(c -> c.getEstado() == EstadoCaja.LIBRE)
                .findFirst()
                .orElse(null);
        
        if (cajaLibre == null) {
            return false; // No hay cajas libres
        }
        
        // Tomar el siguiente socio de la sala de espera
        Socio siguiente = salaEspera.siguienteSocio();
        if (siguiente == null) {
            return false;
        }
        
        // Asignar socio a la caja
        cajaLibre.asignarSocio(siguiente);
        return true;
    }

    public int asignarTodosPosibles(SalaEspera salaEspera, List<Caja> cajas) {
        int asignados = 0;
        while (asignarSocio(salaEspera, cajas)) {
            asignados++;
        }
        return asignados;
    }
}