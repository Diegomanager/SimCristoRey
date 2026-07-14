package com.supermercado.infrastructure.adapter.export;

import com.supermercado.application.cooperativa.port.IReporteExportador;
import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportadorAdapter implements IReporteExportador {

    @Override
    public void exportar(String rutaArchivo,
                          List<ResumenDiario> resumenes,
                          EstadisticasFinancierasService est,
                          long minutosSimulados) throws IOException {

        System.out.println(">>> [Excel] Iniciando exportacion: " + rutaArchivo);

        try (XSSFWorkbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(rutaArchivo)) {

            System.out.println(">>> [Excel] Workbook y FileOutputStream creados.");

            CellStyle estiloTitulo = crearEstiloTitulo(wb);
            CellStyle estiloHeader = crearEstiloHeader(wb);
            CellStyle estiloNumero = crearEstiloNumero(wb, "#,##0");
            CellStyle estiloMoneda = crearEstiloNumero(wb, "#,##0.00");
            CellStyle estiloLabel  = crearEstiloLabel(wb);
            System.out.println(">>> [Excel] Estilos creados.");

            hojaEvolucion(wb, resumenes, estiloHeader, estiloNumero, estiloMoneda);
            System.out.println(">>> [Excel] Hoja Evolucion Diaria creada (" + resumenes.size() + " resumenes).");

            hojaResumenFinal(wb, est, minutosSimulados, estiloTitulo, estiloLabel, estiloMoneda);
            System.out.println(">>> [Excel] Hoja Resumen Final creada.");

            hojaEstadisticasAcumuladas(wb, est, estiloTitulo, estiloLabel, estiloMoneda);
            System.out.println(">>> [Excel] Hoja Estadisticas Acumuladas creada.");

            wb.write(out);
            out.flush();
            System.out.println(">>> [Excel] Workbook escrito y flush realizado. Exportacion OK: " + rutaArchivo);

        } catch (Exception e) {
            System.err.println(">>> [Excel] ERROR durante la exportacion:");
            e.printStackTrace();
            throw new IOException("Error al exportar Excel: " + e.getMessage(), e);
        }
    }

    private void hojaEvolucion(XSSFWorkbook wb, List<ResumenDiario> resumenes,
                                CellStyle header, CellStyle numero, CellStyle moneda) {
        Sheet sh = wb.createSheet("Evolucion Diaria");

        String[] cols = {"Dia", "Laborable", "Generados", "P.Principal", "Rezagados",
                "Total", "No Atend.", "Monto (Bs)", "Espera (min)", "Aten. (min)", "Cajero"};
        Row filaHeader = sh.createRow(0);
        for (int i = 0; i < cols.length; i++) {
            Cell c = filaHeader.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }

        int filaIdx = 1;
        for (ResumenDiario r : resumenes) {
            if (!r.isLaborable()) continue;
            Row fila = sh.createRow(filaIdx++);
            int col = 0;
            fila.createCell(col++).setCellValue(r.getNumeroDia());
            fila.createCell(col++).setCellValue("Si");
            setNum(fila, col++, r.getGenerados(), numero);
            setNum(fila, col++, r.getAtendidosPrincipal(), numero);
            setNum(fila, col++, r.getAtendidosRezagados(), numero);
            setNum(fila, col++, r.getTotalAtendidos(), numero);
            setNum(fila, col++, r.getNoAtendidos(), numero);
            setNum(fila, col++, r.getMontoTotal(), moneda);
            setNum(fila, col++, r.getPromedioEspera(), moneda);
            setNum(fila, col++, r.getPromedioAtencion(), moneda);
            fila.createCell(col).setCellValue(r.getCajeroEstrella() != null ? r.getCajeroEstrella() : "-");
        }

        for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);
        sh.createFreezePane(0, 1);
    }

    private void hojaResumenFinal(XSSFWorkbook wb, EstadisticasFinancierasService est,
                                   long minutosSimulados,
                                   CellStyle titulo, CellStyle label, CellStyle moneda) {
        Sheet sh = wb.createSheet("Resumen Final");
        int fila = 0;

        fila = seccionResumen(sh, fila, "FASE PRINCIPAL (acumulado)", titulo, label, moneda,
                est.getAcumAtendPpal(), est.getAcumMontoPpal(),
                est.getPrincipal().getPromedioEspera(),
                est.getPrincipal().getPromedioAtencion(),
                est.getPrincipal().getCajeroEstrella());

        fila++;
        fila = seccionResumen(sh, fila, "FASE REZAGADOS (acumulado)", titulo, label, moneda,
                est.getAcumAtendRez(), est.getAcumMontoRez(),
                est.getRezagados().getPromedioEspera(),
                est.getRezagados().getPromedioAtencion(),
                est.getRezagados().getCajeroEstrella());

        fila++;
        Row tituloTotal = sh.createRow(fila++);
        Cell ct = tituloTotal.createCell(0);
        ct.setCellValue("TOTAL GLOBAL");
        ct.setCellStyle(titulo);

        fila = filaLabelValor(sh, fila, "Dias laborables:", est.getDiasAcumulados(), label);
        fila = filaLabelValor(sh, fila, "Generados:", est.getAcumGenerados(), label);
        fila = filaLabelValor(sh, fila, "Atendidos:", est.getAcumTotalAtendidos(), label);
        fila = filaLabelValor(sh, fila, "No atendidos:", est.getAcumNoAtendidos(), label);
        fila = filaLabelValorDouble(sh, fila, "Monto total (Bs):", est.getAcumMonto(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Espera promedio (min):", est.getAcumPromedioEspera(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Atencion promedio (min):", est.getAcumPromedioAtencion(), label, moneda);
        fila = filaLabelTexto(sh, fila, "Cajero estrella:", est.getCajeroEstrellaGlobal(), label);
        if (minutosSimulados > 0) {
            fila = filaLabelValorDouble(sh, fila, "Eficiencia global (aten/min):",
                    est.getEficienciaGlobal(minutosSimulados), label, moneda);
        }

        sh.autoSizeColumn(0);
        sh.autoSizeColumn(1);
    }

    private int seccionResumen(Sheet sh, int fila, String tituloTxt, CellStyle titulo,
                                CellStyle label, CellStyle moneda,
                                int atendidos, double monto, double espera, double atencion, String cajero) {
        Row rTitulo = sh.createRow(fila++);
        Cell ct = rTitulo.createCell(0);
        ct.setCellValue(tituloTxt);
        ct.setCellStyle(titulo);

        fila = filaLabelValor(sh, fila, "Atendidos:", atendidos, label);
        fila = filaLabelValorDouble(sh, fila, "Monto (Bs):", monto, label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Espera (min):", espera, label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Atencion (min):", atencion, label, moneda);
        fila = filaLabelTexto(sh, fila, "Cajero:", cajero != null ? cajero : "-", label);
        return fila;
    }

    private void hojaEstadisticasAcumuladas(XSSFWorkbook wb, EstadisticasFinancierasService est,
                                             CellStyle titulo, CellStyle label, CellStyle moneda) {
        Sheet sh = wb.createSheet("Estadisticas Acumuladas");
        int fila = 0;

        Row rTitulo = sh.createRow(fila++);
        Cell ct = rTitulo.createCell(0);
        ct.setCellValue("ESTADISTICAS ACUMULADAS GLOBALES");
        ct.setCellStyle(titulo);
        fila++;

        fila = filaLabelValor(sh, fila, "Dias acumulados:", est.getDiasAcumulados(), label);
        fila = filaLabelValor(sh, fila, "Generados:", est.getAcumGenerados(), label);
        fila = filaLabelValor(sh, fila, "Atendidos Principal:", est.getAcumAtendPpal(), label);
        fila = filaLabelValor(sh, fila, "Atendidos Rezagados:", est.getAcumAtendRez(), label);
        fila = filaLabelValor(sh, fila, "Total atendidos:", est.getAcumTotalAtendidos(), label);
        fila = filaLabelValor(sh, fila, "No atendidos:", est.getAcumNoAtendidos(), label);
        fila = filaLabelValorDouble(sh, fila, "Monto Principal (Bs):", est.getAcumMontoPpal(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Monto Rezagados (Bs):", est.getAcumMontoRez(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Monto total (Bs):", est.getAcumMonto(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Espera promedio (min):", est.getAcumPromedioEspera(), label, moneda);
        fila = filaLabelValorDouble(sh, fila, "Atencion promedio (min):", est.getAcumPromedioAtencion(), label, moneda);
        fila = filaLabelTexto(sh, fila, "Cajero estrella global:", est.getCajeroEstrellaGlobal(), label);

        sh.autoSizeColumn(0);
        sh.autoSizeColumn(1);
    }

    private int filaLabelValor(Sheet sh, int fila, String lbl, int valor, CellStyle label) {
        Row r = sh.createRow(fila);
        Cell c0 = r.createCell(0); c0.setCellValue(lbl); c0.setCellStyle(label);
        r.createCell(1).setCellValue(valor);
        return fila + 1;
    }

    private int filaLabelValorDouble(Sheet sh, int fila, String lbl, double valor, CellStyle label, CellStyle moneda) {
        Row r = sh.createRow(fila);
        Cell c0 = r.createCell(0); c0.setCellValue(lbl); c0.setCellStyle(label);
        Cell c1 = r.createCell(1); c1.setCellValue(valor); if (moneda != null) c1.setCellStyle(moneda);
        return fila + 1;
    }

    private int filaLabelTexto(Sheet sh, int fila, String lbl, String texto, CellStyle label) {
        Row r = sh.createRow(fila);
        Cell c0 = r.createCell(0); c0.setCellValue(lbl); c0.setCellStyle(label);
        r.createCell(1).setCellValue(texto);
        return fila + 1;
    }

    private void setNum(Row fila, int col, double valor, CellStyle estilo) {
        Cell c = fila.createCell(col);
        c.setCellValue(valor);
        c.setCellStyle(estilo);
    }

    private CellStyle crearEstiloTitulo(XSSFWorkbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 13);
        f.setColor(IndexedColors.WHITE.getIndex());
        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }

    private CellStyle crearEstiloHeader(XSSFWorkbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle crearEstiloLabel(XSSFWorkbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        return s;
    }

    private CellStyle crearEstiloNumero(XSSFWorkbook wb, String formato) {
        CellStyle s = wb.createCellStyle();
        s.setDataFormat(wb.createDataFormat().getFormat(formato));
        return s;
    }
}