package com.supermercado.application.supermercado.usecase;

import com.supermercado.domain.supermercado.model.Caja;
import com.supermercado.domain.supermercado.service.DiagnosticoService;
import com.supermercado.application.supermercado.dto.DiagnosticoDTO;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosticarConfiguracionUseCase {
    
    private final DiagnosticoService diagnosticoService;
    
    public DiagnosticarConfiguracionUseCase() {
        this.diagnosticoService = new DiagnosticoService();
    }
    
    public DiagnosticoDTO ejecutar(List<Caja> cajas) {
        List<DiagnosticoService.Diagnostico> diagnosticos = 
            diagnosticoService.diagnosticar(cajas);
        
        List<DiagnosticoDTO.DiagnosticoItem> items = diagnosticos.stream()
            .map(d -> new DiagnosticoDTO.DiagnosticoItem(
                d.getCajaId(),
                d.getNivel().name(),
                d.getNivel().icono,
                d.getMensaje(),
                d.getSugerencia(),
                d.getColaActual(),
                d.getColaMaxima()
            ))
            .collect(Collectors.toList());
        
        long criticos = diagnosticos.stream()
            .filter(d -> d.getNivel() == DiagnosticoService.NivelAlerta.CRITICO).count();
        long alertas = diagnosticos.stream()
            .filter(d -> d.getNivel() == DiagnosticoService.NivelAlerta.ALERTA).count();
        long atencion = diagnosticos.stream()
            .filter(d -> d.getNivel() == DiagnosticoService.NivelAlerta.ATENCION).count();
        
        String resumen = generarResumen(criticos, alertas, atencion);
        
        return new DiagnosticoDTO(items, criticos, alertas, atencion, resumen);
    }
    
    private String generarResumen(long criticos, long alertas, long atencion) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESUMEN DEL DIAGNOSTICO:\n");
        sb.append("  Criticos: ").append(criticos).append("\n");
        sb.append("  Alertas: ").append(alertas).append("\n");
        sb.append("  Atencion: ").append(atencion).append("\n");
        
        if (criticos > 0) {
            sb.append("  ESTADO: CRITICO - Se requieren cambios urgentes");
        } else if (alertas > 0) {
            sb.append("  ESTADO: ALERTA - Se recomienda optimizacion");
        } else if (atencion > 0) {
            sb.append("  ESTADO: ATENCION - Monitorear el sistema");
        } else {
            sb.append("  ESTADO: NORMAL - Sistema funcionando correctamente");
        }
        
        return sb.toString();
    }
}