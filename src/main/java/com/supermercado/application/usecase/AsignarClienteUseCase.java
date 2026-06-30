package com.supermercado.application.usecase;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;
import com.supermercado.domain.service.SimuladorService;
import com.supermercado.domain.service.RelojSimulacionService;

import java.util.List;

public class AsignarClienteUseCase {
    
    private final SimuladorService simuladorService;
    
    public AsignarClienteUseCase() {
        this.simuladorService = new SimuladorService(new RelojSimulacionService());
    }
    
    public void ejecutar(List<Caja> cajas, Cliente cliente) {
        if (cajas == null || cajas.isEmpty()) {
            throw new IllegalArgumentException("La lista de cajas no puede estar vac?a");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        simuladorService.asignarCliente(cajas, cliente);
    }
}
