package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;
import com.supermercado.domain.supermercado.service.SimuladorService;
import com.supermercado.domain.supermercado.service.RelojSimulacionService;

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
