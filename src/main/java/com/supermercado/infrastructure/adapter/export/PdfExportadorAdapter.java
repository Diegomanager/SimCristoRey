package com.supermercado.infrastructure.adapter.export;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.supermercado.application.cooperativa.port.IReporteExportador;
import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;

import java.io.IOException;
import java.util.*;

public class PdfExportadorAdapter implements IReporteExportador {

    private static final DeviceRgb AZUL_OSCURO = new DeviceRgb(30, 60, 110);
    private static final DeviceRgb AZUL_CLARO  = new DeviceRgb(220, 230, 245);
    private static final DeviceRgb GRIS_CLARO  = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb BLANCO      = new DeviceRgb(255, 255, 255);

    private static final List<String> CODIGOS = List.of("C","A","S","F","P","R","PC","PS","PA","PP");
    private static final Map<String,String> NOMBRES = Map.ofEntries(
            Map.entry("C","Ahorro/Credito"), Map.entry("A","Semapa (Agua)"),
            Map.entry("S","Elfec-Comteco-Semapa"), Map.entry("F","Fraccionamiento"),
            Map.entry("P","Plataforma"), Map.entry("R","Renta Dignidad"),
            Map.entry("PC","Pref. Ahorro-Credito"), Map.entry("PS","Pref. Elfec-Comteco-Semapa"),
            Map.entry("PA","Pref. Semapa"), Map.entry("PP","Pref. Plataforma"));

    @Override
    public void exportar(String rutaArchivo,
                          List<ResumenDiario> resumenes,
                          EstadisticasFinancierasService est,
                          long minutosSimulados) throws IOException {

        System.out.println(">>> [PDF] Iniciando exportacion: " + rutaArchivo);

        try (PdfWriter writer = new PdfWriter(rutaArchivo);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc, PageSize.A4)) {

            doc.setMargins(30, 30, 30, 30);

            escribirCabecera(doc);
            escribirTablaEvolucion(doc, resumenes);
            escribirResumenFinal(doc, est, minutosSimulados);
            escribirEstadisticasAcumuladas(doc, est);
            escribirDesgloseServicio(doc, est);

            System.out.println(">>> [PDF] Exportacion completada: " + rutaArchivo);

        } catch (Exception e) {
            System.err.println(">>> [PDF] ERROR:");
            e.printStackTrace();
            throw new IOException("Error al exportar PDF: " + e.getMessage(), e);
        }
    }

    private void escribirCabecera(Document doc) {
        Paragraph titulo = new Paragraph("Reporte de Simulacion - SimCristoRey")
                .setFontSize(18).setBold()
                .setFontColor(AZUL_OSCURO)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(titulo);

        Paragraph subtitulo = new Paragraph("Cooperativa de Ahorro y Credito Cristo Rey")
                .setFontSize(11).setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        doc.add(subtitulo);
    }

    private void escribirTablaEvolucion(Document doc, List<ResumenDiario> resumenes) {
        doc.add(tituloSeccion("Evolucion Diaria"));

        float[] anchos = {60, 55, 55, 55, 45, 75, 55, 55, 80};
        Table tabla = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();

        String[] cols = {"Dia/Fecha", "Gen.", "Ppal.", "Rezag.", "Total", "Monto (Bs)", "Espera", "Aten.", "Cajero"};
        for (String c : cols) tabla.addHeaderCell(celdaHeader(c));

        boolean par = false;
        for (ResumenDiario r : resumenes) {
            if (!r.isLaborable()) continue;
            DeviceRgb fondo = par ? GRIS_CLARO : BLANCO;
            par = !par;

            String etiquetaDia = r.getFecha() != null ? r.getFecha().toString() : ("Dia " + r.getNumeroDia());
            tabla.addCell(celdaDato(etiquetaDia, fondo));
            tabla.addCell(celdaDato(String.valueOf(r.getGenerados()), fondo));
            tabla.addCell(celdaDato(String.valueOf(r.getAtendidosPrincipal()), fondo));
            tabla.addCell(celdaDato(String.valueOf(r.getAtendidosRezagados()), fondo));
            tabla.addCell(celdaDato(String.valueOf(r.getTotalAtendidos()), fondo));
            tabla.addCell(celdaDato(String.format(Locale.ROOT, "%,.2f", r.getMontoTotal()), fondo));
            tabla.addCell(celdaDato(String.format(Locale.ROOT, "%.1f", r.getPromedioEspera()), fondo));
            tabla.addCell(celdaDato(String.format(Locale.ROOT, "%.1f", r.getPromedioAtencion()), fondo));
            tabla.addCell(celdaDato(r.getCajeroEstrella() != null ? r.getCajeroEstrella() : "-", fondo));
        }

        doc.add(tabla);
    }

    private void escribirResumenFinal(Document doc, EstadisticasFinancierasService est, long minutosSimulados) {
        doc.add(tituloSeccion("Resumen Final"));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();

        agregarSeccionFase(tabla, "FASE PRINCIPAL (acumulado)",
                est.getAcumAtendPpal(), est.getAcumMontoPpal(),
                est.getPrincipal().getPromedioEspera(), est.getPrincipal().getPromedioAtencion(),
                est.getPrincipal().getCajeroEstrella());

        agregarSeccionFase(tabla, "FASE REZAGADOS (acumulado)",
                est.getAcumAtendRez(), est.getAcumMontoRez(),
                est.getRezagados().getPromedioEspera(), est.getRezagados().getPromedioAtencion(),
                est.getRezagados().getCajeroEstrella());

        doc.add(tabla);

        Table totalTabla = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        totalTabla.addCell(celdaLabel("Dias laborables:")); totalTabla.addCell(celdaValor(String.valueOf(est.getDiasAcumulados())));
        totalTabla.addCell(celdaLabel("Generados:")); totalTabla.addCell(celdaValor(String.valueOf(est.getAcumGenerados())));
        totalTabla.addCell(celdaLabel("Atendidos:")); totalTabla.addCell(celdaValor(String.valueOf(est.getAcumTotalAtendidos())));
        totalTabla.addCell(celdaLabel("No atendidos:")); totalTabla.addCell(celdaValor(String.valueOf(est.getAcumNoAtendidos())));
        totalTabla.addCell(celdaLabel("Monto total (Bs):")); totalTabla.addCell(celdaValor(String.format(Locale.ROOT, "%,.2f", est.getAcumMonto())));
        totalTabla.addCell(celdaLabel("Espera promedio (min):")); totalTabla.addCell(celdaValor(String.format(Locale.ROOT, "%.1f", est.getAcumPromedioEspera())));
        totalTabla.addCell(celdaLabel("Atencion promedio (min):")); totalTabla.addCell(celdaValor(String.format(Locale.ROOT, "%.1f", est.getAcumPromedioAtencion())));
        totalTabla.addCell(celdaLabel("Cajero estrella:")); totalTabla.addCell(celdaValor(est.getCajeroEstrellaGlobal()));
        if (minutosSimulados > 0) {
            totalTabla.addCell(celdaLabel("Eficiencia global (aten/min):"));
            totalTabla.addCell(celdaValor(String.format(Locale.ROOT, "%.3f", est.getEficienciaGlobal(minutosSimulados))));
        }

        doc.add(new Paragraph("Total Global").setBold().setFontSize(12).setMarginTop(8));
        doc.add(totalTabla);
    }

    private void agregarSeccionFase(Table tabla, String titulo, int atendidos, double monto,
                                     double espera, double atencion, String cajero) {
        Cell header = new Cell(1, 2).add(new Paragraph(titulo).setBold())
                .setBackgroundColor(AZUL_CLARO);
        tabla.addCell(header);

        tabla.addCell(celdaLabel("Atendidos:")); tabla.addCell(celdaValor(String.valueOf(atendidos)));
        tabla.addCell(celdaLabel("Monto (Bs):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%,.2f", monto)));
        tabla.addCell(celdaLabel("Espera (min):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%.1f", espera)));
        tabla.addCell(celdaLabel("Atencion (min):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%.1f", atencion)));
        tabla.addCell(celdaLabel("Cajero:")); tabla.addCell(celdaValor(cajero != null ? cajero : "-"));
    }

    private void escribirEstadisticasAcumuladas(Document doc, EstadisticasFinancierasService est) {
        doc.add(tituloSeccion("Estadisticas Acumuladas Globales"));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        tabla.addCell(celdaLabel("Dias acumulados:")); tabla.addCell(celdaValor(String.valueOf(est.getDiasAcumulados())));
        tabla.addCell(celdaLabel("Generados:")); tabla.addCell(celdaValor(String.valueOf(est.getAcumGenerados())));
        tabla.addCell(celdaLabel("Atendidos Principal:")); tabla.addCell(celdaValor(String.valueOf(est.getAcumAtendPpal())));
        tabla.addCell(celdaLabel("Atendidos Rezagados:")); tabla.addCell(celdaValor(String.valueOf(est.getAcumAtendRez())));
        tabla.addCell(celdaLabel("Monto Principal (Bs):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%,.2f", est.getAcumMontoPpal())));
        tabla.addCell(celdaLabel("Monto Rezagados (Bs):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%,.2f", est.getAcumMontoRez())));
        tabla.addCell(celdaLabel("Monto total (Bs):")); tabla.addCell(celdaValor(String.format(Locale.ROOT, "%,.2f", est.getAcumMonto())));
        tabla.addCell(celdaLabel("Cajero estrella global:")); tabla.addCell(celdaValor(est.getCajeroEstrellaGlobal()));

        doc.add(tabla);
    }

    /** NUEVO: cuantos socios de cada tipo de ticket se atendieron en total. */
    private void escribirDesgloseServicio(Document doc, EstadisticasFinancierasService est) {
        doc.add(tituloSeccion("Desglose por Tipo de Servicio"));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{20, 55, 25})).useAllAvailableWidth();
        tabla.addHeaderCell(celdaHeader("Codigo"));
        tabla.addHeaderCell(celdaHeader("Descripcion"));
        tabla.addHeaderCell(celdaHeader("Atendidos"));

        Map<String,Integer> desglose = est.getAcumAtendidosPorCodigo();
        boolean par = false;
        for (String codigo : CODIGOS) {
            DeviceRgb fondo = par ? GRIS_CLARO : BLANCO;
            par = !par;
            int cantidad = desglose.getOrDefault(codigo, 0);
            tabla.addCell(celdaDato(codigo, fondo));
            tabla.addCell(celdaDato(NOMBRES.getOrDefault(codigo, codigo), fondo));
            tabla.addCell(celdaDato(String.valueOf(cantidad), fondo));
        }
        doc.add(tabla);
    }

    private Paragraph tituloSeccion(String texto) {
        return new Paragraph(texto)
                .setFontSize(14).setBold()
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(AZUL_OSCURO)
                .setPadding(6)
                .setMarginTop(16).setMarginBottom(8);
    }

    private Cell celdaHeader(String texto) {
        return new Cell().add(new Paragraph(texto).setBold().setFontSize(9)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(AZUL_OSCURO)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(4);
    }

    private Cell celdaDato(String texto, DeviceRgb fondo) {
        return new Cell().add(new Paragraph(texto).setFontSize(9))
                .setBackgroundColor(fondo)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(3);
    }

    private Cell celdaLabel(String texto) {
        return new Cell().add(new Paragraph(texto).setBold().setFontSize(10))
                .setPadding(4);
    }

    private Cell celdaValor(String texto) {
        return new Cell().add(new Paragraph(texto).setFontSize(10))
                .setPadding(4);
    }
}