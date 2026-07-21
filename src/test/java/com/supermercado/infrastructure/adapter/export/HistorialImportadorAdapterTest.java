package com.supermercado.infrastructure.adapter.export;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.dto.RegistroAtencion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class HistorialImportadorAdapterTest {

    @TempDir
    Path tempDir;

    @Test
    void testImportarArchivoValido() throws IOException {
        // Crear un Excel de prueba
        File excel = tempDir.resolve("test.xlsx").toFile();
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

            try (FileOutputStream fos = new FileOutputStream(excel)) {
                wb.write(fos);
            }
        }

        HistorialImportadorAdapter adapter = new HistorialImportadorAdapter();
        CalibracionMensual cal = adapter.importar(excel);

        assertNotNull(cal);
        assertTrue(cal.isTieneFechas());
        assertEquals(1, cal.getRegistros().size());
        RegistroAtencion r = cal.getRegistros().get(0);
        assertEquals(LocalDate.of(2026, 3, 2), r.getFecha());
        assertEquals(5, r.getDuracionMinutos());
        assertEquals("C", r.getCodigoServicio());
        assertFalse(r.isEsPreferencial());
    }
}