package com.supermercado.infrastructure.service;

import com.supermercado.application.supermercado.dto.EstadisticasDTO;
import com.supermercado.application.supermercado.port.IReporteExportador;
import com.supermercado.application.supermercado.port.ILogService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador de infraestructura: exporta estadisticas a un archivo de texto plano.
 *
 * Responsabilidad unica: serializar las estadisticas de simulacion
 * a formato texto y persistirlas en el sistema de archivos.
 *
 * NOTA: El nombre anterior "ReporteExcelImpl" era enganoso porque
 * la implementacion genera texto plano, no un archivo Excel (.xlsx).
 * Se renombra a ReporteTextoImpl para reflejar la realidad.
 */
public class ReporteTextoImpl implements IReporteExportador {

    private static final DateTimeFormatter FORMATO_FECHA =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ILogService logService;

    public ReporteTextoImpl(ILogService logService) {
        this.logService = logService;
    }

    @Override
    public void exportar(EstadisticasDTO estadisticas, String rutaArchivo) {
        validarParametros(estadisticas, rutaArchivo);
        escribirReporte(estadisticas, rutaArchivo);
    }

    private void validarParametros(EstadisticasDTO estadisticas, String rutaArchivo) {
        if (estadisticas == null) {
            throw new IllegalArgumentException("Las estadisticas no pueden ser nulas");
        }
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacia");
        }
    }

    private void escribirReporte(EstadisticasDTO estadisticas, String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            writer.println("=== REPORTE DE SIMULACION ===");
            writer.println("Fecha: " + LocalDateTime.now().format(FORMATO_FECHA));
            writer.println();
            writer.println("ESTADISTICAS GENERALES");
            writer.println("----------------------------------------");
            writer.println("Total clientes atendidos : " + estadisticas.getTotalClientesAtendidos());
            writer.println("Total articulos vendidos : " + estadisticas.getTotalArticulosVendidos());
            writer.println("Total minutos de atencion: " + estadisticas.getTotalMinutosAtencion());
            writer.println("Clientes en cola         : " + estadisticas.getClientesEnCola());
            writer.println("Cajero Estrella          : " + estadisticas.getCajeroEstrella());
            writer.println(String.format("Tiempo promedio atencion : %.2f min",
                estadisticas.getTiempoPromedioAtencion()));
            writer.println(String.format("Articulos promedio       : %.2f",
                estadisticas.getArticulosPromedio()));
            writer.println("----------------------------------------");

            logService.info("Reporte exportado a: " + rutaArchivo);

        } catch (IOException e) {
            logService.error("Error al exportar reporte a " + rutaArchivo, e);
        }
    }
}