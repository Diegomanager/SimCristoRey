package com.supermercado.application.cooperativa.usecase;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.dto.RegistroAtencion;
import com.supermercado.application.cooperativa.port.IHistorialImportador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalibrarConfiguracionUseCaseTest {

    @Mock
    private IHistorialImportador importador;

    @InjectMocks
    private CalibrarConfiguracionUseCase useCase;

    @Test
    void ejecutar_delegaAlImportadorYDevuelveSuResultado() throws IOException {
        List<RegistroAtencion> registros = new ArrayList<>();
        registros.add(new RegistroAtencion(LocalDate.now(), LocalTime.now(), 5, "C", false, 100.0));
        CalibracionMensual calMock = new CalibracionMensual(
            new ArrayList<>(), new HashMap<>(), 1, 0, true,
            List.of(LocalDate.now()), new HashMap<>(), 1, 1.0, registros
        );

        when(importador.importar(any(File.class))).thenReturn(calMock);

        File archivo = new File("test.xlsx");
        CalibracionMensual resultado = useCase.ejecutar(archivo);

        assertNotNull(resultado);
        assertEquals(1, resultado.getRegistros().size());
        verify(importador).importar(archivo);
    }

    @Test
    void ejecutar_propagaIOExceptionDelImportador() throws IOException {
        when(importador.importar(any(File.class))).thenThrow(new IOException("archivo invalido"));
        File archivo = new File("roto.xlsx");
        assertThrows(IOException.class, () -> useCase.ejecutar(archivo));
    }
}