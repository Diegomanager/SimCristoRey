package com.supermercado.application.supermercado.usecase;

import com.supermercado.application.supermercado.dto.DiagnosticoDTO;
import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticarConfiguracionUseCaseTest {
    
    private DiagnosticarConfiguracionUseCase useCase;
    private List<Caja> cajas;
    
    @BeforeEach
    void setUp() {
        useCase = new DiagnosticarConfiguracionUseCase();
        cajas = new ArrayList<>();
    }
    
    @Test
    void testEjecutar_DevuelveDiagnostico() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 10; i++) {
            caja.agregarCliente(new Cliente(i, 5));
        }
        cajas.add(caja);
        
        DiagnosticoDTO resultado = useCase.ejecutar(cajas);
        
        assertNotNull(resultado);
        assertFalse(resultado.getItems().isEmpty());
    }
    
    @Test
    void testEjecutar_DetectaCriticos() {
        Caja caja = new Caja(1, false);
        for (int i = 0; i < 50; i++) {
            caja.agregarCliente(new Cliente(i, 5));
        }
        cajas.add(caja);
        
        DiagnosticoDTO resultado = useCase.ejecutar(cajas);
        
        assertTrue(resultado.getCriticos() > 0);
        assertTrue(resultado.tieneProblemas());
        assertEquals("CRITICO", resultado.getEstadoGeneral());
    }
}