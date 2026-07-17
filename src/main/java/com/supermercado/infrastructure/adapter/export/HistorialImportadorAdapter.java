package com.supermercado.infrastructure.adapter.export;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.dto.RegistroAtencion;
import com.supermercado.application.cooperativa.port.IHistorialImportador;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HistorialImportadorAdapter implements IHistorialImportador {

    private static final DateTimeFormatter[] FORMATOS_HORA = {
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm")
    };
    private static final DateTimeFormatter[] FORMATOS_FECHA_HORA = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
    };

    private static final Map<String, String> MAPA_CODIGOS = new HashMap<>();
    static {
        MAPA_CODIGOS.put("1-0", "C");  MAPA_CODIGOS.put("1-1", "PC");
        MAPA_CODIGOS.put("2-0", "P");  MAPA_CODIGOS.put("2-1", "PP");
        MAPA_CODIGOS.put("3-0", "S");  MAPA_CODIGOS.put("3-1", "PS");
        MAPA_CODIGOS.put("4-0", "A");  MAPA_CODIGOS.put("4-1", "PA");
        MAPA_CODIGOS.put("5-0", "F");  MAPA_CODIGOS.put("5-1", "F");
        MAPA_CODIGOS.put("6-0", "R");  MAPA_CODIGOS.put("6-1", "R");
    }

    private static final Map<String, String> NOMBRES = new LinkedHashMap<>();
    static {
        NOMBRES.put("C",  "Socios Ahorro/Credito");
        NOMBRES.put("A",  "Socios Semapa (Agua)");
        NOMBRES.put("S",  "Socios Elfec-Comteco-Semapa");
        NOMBRES.put("F",  "Socios Fraccionamiento");
        NOMBRES.put("P",  "Socios Plataforma");
        NOMBRES.put("R",  "Renta Dignidad");
        NOMBRES.put("PC", "Preferente Ahorro-Credito");
        NOMBRES.put("PS", "Preferente Elfec-Comteco-Semapa");
        NOMBRES.put("PA", "Preferente Semapa");
        NOMBRES.put("PP", "Preferente Plataforma");
    }

    private static final Map<String, double[]> MONTOS_DEFECTO = new HashMap<>();
    static {
        MONTOS_DEFECTO.put("C",  new double[]{500,    200000});
        MONTOS_DEFECTO.put("A",  new double[]{30,     200});
        MONTOS_DEFECTO.put("S",  new double[]{20,     300});
        MONTOS_DEFECTO.put("F",  new double[]{200,    5000});
        MONTOS_DEFECTO.put("P",  new double[]{50,     500});
        MONTOS_DEFECTO.put("R",  new double[]{250,    250});
        MONTOS_DEFECTO.put("PC", new double[]{500,    200000});
        MONTOS_DEFECTO.put("PS", new double[]{20,     300});
        MONTOS_DEFECTO.put("PA", new double[]{30,     200});
        MONTOS_DEFECTO.put("PP", new double[]{50,     500});
    }

    @Override
    public CalibracionMensual importar(File archivo) throws IOException {
        System.out.println(">>> [Importar] Leyendo historial: " + archivo.getAbsolutePath());

        Map<String, List<Integer>> duracionesPorCodigo = new LinkedHashMap<>();
        Map<LocalDate, List<LocalDateTime>> instantesPorDia = new TreeMap<>();
        List<RegistroAtencion> registros = new ArrayList<>();
        int totalFichas = 0;
        int filasIgnoradas = 0;
        boolean colFechaExiste;

        try (FileInputStream fis = new FileInputStream(archivo);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            Sheet sh = wb.getSheetAt(0);
            if (sh == null || sh.getLastRowNum() < 1) {
                throw new IOException("El archivo no tiene datos.");
            }

            Row header = sh.getRow(0);
            int colId     = indiceColumna(header, "id_tipo_servicio");
            int colPref   = indiceColumna(header, "es_preferencial");
            int colInicio = indiceColumna(header, "hora_inicio_atencion");
            int colFin    = indiceColumna(header, "hora_fin_atencion");
            int colFecha  = indiceColumna(header, "fecha_creacion");
            colFechaExiste = colFecha >= 0;

            if (colId < 0 || colPref < 0 || colInicio < 0 || colFin < 0) {
                throw new IOException("Faltan columnas obligatorias: id_tipo_servicio, "
                        + "es_preferencial, hora_inicio_atencion, hora_fin_atencion.");
            }
            if (!colFechaExiste) {
                System.out.println(">>> [Importar] AVISO: no se encontro 'fecha_creacion'. "
                        + "Sin fechas no hay replay posible, solo calibracion de servicios/probabilidades.");
            }

            for (int r = 1; r <= sh.getLastRowNum(); r++) {
                Row fila = sh.getRow(r);
                if (fila == null) continue;
                try {
                    Integer idTipo = leerEntero(fila.getCell(colId));
                    Integer esPref = leerEntero(fila.getCell(colPref));
                    LocalTime inicio = leerHora(fila.getCell(colInicio));
                    LocalTime fin    = leerHora(fila.getCell(colFin));

                    if (idTipo == null || esPref == null || inicio == null || fin == null) {
                        filasIgnoradas++; continue;
                    }

                    long minutos = Duration.between(inicio, fin).toMinutes();
                    if (minutos < 0) minutos += 24 * 60;
                    if (minutos <= 0) minutos = 1;

                    String clave = idTipo + "-" + (esPref != 0 ? "1" : "0");
                    String codigo = MAPA_CODIGOS.get(clave);
                    if (codigo == null) { filasIgnoradas++; continue; }

                    duracionesPorCodigo.computeIfAbsent(codigo, k -> new ArrayList<>()).add((int) minutos);
                    totalFichas++;

                    if (colFechaExiste) {
                        LocalDateTime instante = leerFechaHora(fila.getCell(colFecha));
                        if (instante != null) {
                            instantesPorDia.computeIfAbsent(instante.toLocalDate(), k -> new ArrayList<>())
                                    .add(instante);

                            double[] montos = MONTOS_DEFECTO.getOrDefault(codigo, new double[]{0, 1000});
                            double montoMedio = (montos[0] + montos[1]) / 2.0;
                            registros.add(new RegistroAtencion(
                                    instante.toLocalDate(), instante.toLocalTime(),
                                    (int) minutos, codigo, esPref != 0, montoMedio));
                        }
                    }
                } catch (Exception filaEx) {
                    filasIgnoradas++;
                }
            }
        }

        if (totalFichas == 0) throw new IOException("No se pudo procesar ninguna fila valida.");

        List<ServicioFinanciero> servicios = new ArrayList<>();
        Map<String, Double> probabilidades = new LinkedHashMap<>();
        int contador = 1;
        for (Map.Entry<String, List<Integer>> e : duracionesPorCodigo.entrySet()) {
            String codigo = e.getKey();
            List<Integer> duraciones = e.getValue();
            int min = Collections.min(duraciones);
            int max = Collections.max(duraciones);
            if (min == max) max = min + 1;
            int frecuencia = duraciones.size();
            double probabilidad = (double) frecuencia / totalFichas;
            double[] montos = MONTOS_DEFECTO.getOrDefault(codigo, new double[]{0, 1000});

            ServicioFinanciero s = new ServicioFinanciero();
            s.setId("SVC-" + codigo + "-IMP" + (contador++));
            s.setNombre(NOMBRES.getOrDefault(codigo, codigo));
            s.setTipoCajaRequerido(codigo);
            s.setDuracionMinima(min);
            s.setDuracionMaxima(max);
            s.setMontoMinimo(montos[0]);
            s.setMontoMaximo(montos[1]);
            s.setTasaInteres(0.1);
            s.setActivo(true);

            servicios.add(s);
            probabilidades.put(codigo, probabilidad);
        }

        List<LocalDate> diasLaborables = new ArrayList<>();
        Map<LocalDate, Integer> sociosPorDia = new LinkedHashMap<>();
        int maxSociosDia = 0;
        double intervaloPromedio = 0;

        if (colFechaExiste && !instantesPorDia.isEmpty()) {
            List<Long> diffsMinutos = new ArrayList<>();
            for (Map.Entry<LocalDate, List<LocalDateTime>> e : instantesPorDia.entrySet()) {
                LocalDate dia = e.getKey();
                List<LocalDateTime> instantes = e.getValue();
                Collections.sort(instantes);
                diasLaborables.add(dia);
                sociosPorDia.put(dia, instantes.size());
                maxSociosDia = Math.max(maxSociosDia, instantes.size());
                for (int i = 1; i < instantes.size(); i++) {
                    long diff = Duration.between(instantes.get(i - 1), instantes.get(i)).toMinutes();
                    if (diff >= 0) diffsMinutos.add(diff);
                }
            }
            if (!diffsMinutos.isEmpty()) {
                double suma = 0;
                for (long d : diffsMinutos) suma += d;
                intervaloPromedio = suma / diffsMinutos.size();
            }
        }

        // Ordenar registros por fecha y hora, para que el replay procese en orden real
        registros.sort(Comparator.comparing(RegistroAtencion::getFecha)
                .thenComparing(RegistroAtencion::getHoraLlegada));

        System.out.println(">>> [Importar] Fichas: " + totalFichas + " | Ignoradas: " + filasIgnoradas
                + " | Servicios: " + servicios.size() + " | Dias: " + diasLaborables.size()
                + " | MaxSocios/dia: " + maxSociosDia
                + " | Intervalo: " + String.format(Locale.ROOT, "%.2f", intervaloPromedio) + " min"
                + " | Registros para replay: " + registros.size());

        return new CalibracionMensual(servicios, probabilidades, totalFichas, filasIgnoradas,
                colFechaExiste && !instantesPorDia.isEmpty(),
                diasLaborables, sociosPorDia, maxSociosDia, intervaloPromedio, registros);
    }

    private int indiceColumna(Row header, String nombreBuscado) {
        if (header == null) return -1;
        for (Cell c : header) {
            if (c.getCellType() == CellType.STRING) {
                String valor = c.getStringCellValue().trim().toLowerCase(Locale.ROOT);
                if (valor.equals(nombreBuscado.toLowerCase(Locale.ROOT))) return c.getColumnIndex();
            }
        }
        return -1;
    }

    private Integer leerEntero(Cell c) {
        if (c == null) return null;
        try {
            if (c.getCellType() == CellType.NUMERIC) return (int) c.getNumericCellValue();
            if (c.getCellType() == CellType.STRING) return Integer.parseInt(c.getStringCellValue().trim());
        } catch (Exception ignored) {}
        return null;
    }

    private LocalTime leerHora(Cell c) {
        if (c == null) return null;
        try {
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                return c.getLocalDateTimeCellValue().toLocalTime();
            }
            if (c.getCellType() == CellType.STRING) {
                String texto = c.getStringCellValue().trim();
                for (DateTimeFormatter f : FORMATOS_HORA) {
                    try { return LocalTime.parse(texto, f); } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private LocalDateTime leerFechaHora(Cell c) {
        if (c == null) return null;
        try {
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                return c.getLocalDateTimeCellValue();
            }
            if (c.getCellType() == CellType.STRING) {
                String texto = c.getStringCellValue().trim();
                for (DateTimeFormatter f : FORMATOS_FECHA_HORA) {
                    try { return LocalDateTime.parse(texto, f); } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}