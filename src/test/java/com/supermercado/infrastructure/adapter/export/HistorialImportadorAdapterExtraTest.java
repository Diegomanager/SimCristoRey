package com.supermercado.infrastructure.adapter.export;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HistorialImportadorAdapterExtraTest {

    @TempDir
    Path tempDir;

    @Test
    void importar_sinColumnaFechaCreacion_calibraServiciosPeroNoFechas() throws IOException {
        File excel = tempDir.resolve("sin_fecha.xlsx").toFile();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Datos");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("id_tipo_servicio");
            header.createCell(1).setCellValue("es_preferencial");
            header.createCell(2).setCellValue("hora_inicio_atencion");
            header.createCell(3).setCellValue("hora_fin_atencion");

            Row row1 = sh.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue(0);
            row1.createCell(2).setCellValue("08:30:00");
            row1.createCell(3).setCellValue("08:35:00");

            try (FileOutputStream fos = new FileOutputStream(excel)) {
                wb.write(fos);
            }
        }

        HistorialImportadorAdapter adapter = new HistorialImportadorAdapter();
        CalibracionMensual cal = adapter.importar(excel);

        assertNotNull(cal);
        assertFalse(cal.isTieneFechas());
        assertTrue(cal.getRegistros().isEmpty());
        assertEquals(1, cal.getServicios().size());
    }

    @Test
    void importar_conFilaInvalida_laIgnoraYProcesaElResto() throws IOException {
        File excel = tempDir.resolve("invalidas.xlsx").toFile();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Datos");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("id_tipo_servicio");
            header.createCell(1).setCellValue("es_preferencial");
            header.createCell(2).setCellValue("hora_inicio_atencion");
            header.createCell(3).setCellValue("hora_fin_atencion");
            header.createCell(4).setCellValue("fecha_creacion");

            Row row1 = sh.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue(0);
            row1.createCell(2).setCellValue("08:30:00");
            row1.createCell(3).setCellValue("08:35:00");
            row1.createCell(4).setCellValue("2026-03-02 08:30:00");

            Row row2 = sh.createRow(2);
            row2.createCell(0).setCellValue(1);
            row2.createCell(1).setCellValue(0);
            row2.createCell(2).setCellValue("");
            row2.createCell(3).setCellValue("08:40:00");
            row2.createCell(4).setCellValue("2026-03-02 08:30:00");

            try (FileOutputStream fos = new FileOutputStream(excel)) {
                wb.write(fos);
            }
        }

        HistorialImportadorAdapter adapter = new HistorialImportadorAdapter();
        CalibracionMensual cal = adapter.importar(excel);

        assertEquals(1, cal.getTotalFichasProcesadas());
        assertEquals(1, cal.getFilasIgnoradas());
    }

    @Test
    void importar_conDuracionCero_seFuerzaAMinimoUnMinuto() throws IOException {
        File excel = tempDir.resolve("duracion_cero.xlsx").toFile();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Datos");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("id_tipo_servicio");
            header.createCell(1).setCellValue("es_preferencial");
            header.createCell(2).setCellValue("hora_inicio_atencion");
            header.createCell(3).setCellValue("hora_fin_atencion");
            header.createCell(4).setCellValue("fecha_creacion");

            Row row1 = sh.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue(0);
            row1.createCell(2).setCellValue("08:30:00");
            row1.createCell(3).setCellValue("08:30:00");
            row1.createCell(4).setCellValue("2026-03-02 08:30:00");

            try (FileOutputStream fos = new FileOutputStream(excel)) {
                wb.write(fos);
            }
        }

        HistorialImportadorAdapter adapter = new HistorialImportadorAdapter();
        CalibracionMensual cal = adapter.importar(excel);

        assertEquals(1, cal.getRegistros().size());
        assertEquals(1, cal.getRegistros().get(0).getDuracionMinutos());
    }

    @Test
    void importar_archivoSinColumnasObligatorias_lanzaIOException() throws IOException {
        File excel = tempDir.resolve("sin_columnas.xlsx").toFile();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Datos");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("otra_columna");
            Row row1 = sh.createRow(1);
            row1.createCell(0).setCellValue("x");
            try (FileOutputStream fos = new FileOutputStream(excel)) {
                wb.write(fos);
            }
        }

        HistorialImportadorAdapter adapter = new HistorialImportadorAdapter();
        assertThrows(IOException.class, () -> adapter.importar(excel));
    }
}