package com.supermercado.infrastructure.adapter.export;

import com.supermercado.application.cooperativa.port.IReporteExportador;
import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TxtExportadorAdapter implements IReporteExportador {

    private static final String LINEA = "=".repeat(78);
    private static final String SUBLINEA = "-".repeat(78);

    @Override
    public void exportar(String rutaArchivo,
                         List<ResumenDiario> resumenes,
                         EstadisticasFinancierasService est,
                         long minutosSimulados) throws IOException {

        try (FileWriter w = new FileWriter(rutaArchivo, false)) {
            w.write(LINEA + "\n");
            w.write(centrar("REPORTE DE SIMULACION - SIMCRISTOREY") + "\n");
            w.write(LINEA + "\n\n");

            escribirEvolucion(w, resumenes);
            w.write("\n");
            escribirResumenFinal(w, est);
            w.write("\n");
            escribirEstadisticasAcumuladas(w, est, minutosSimulados);
        }
    }

    private void escribirEvolucion(FileWriter w, List<ResumenDiario> resumenes) throws IOException {
        w.write("EVOLUCION DIARIA\n");
        w.write(SUBLINEA + "\n");
        w.write(String.format("%-5s | %-9s | %-9s | %-9s | %-8s | %-12s | %-9s | %-9s | %-20s%n",
                "Dia", "Generados", "Principal", "Rezagados", "Total", "Monto (Bs)", "Espera", "Aten.", "Cajero"));
        w.write(SUBLINEA + "\n");
        for (ResumenDiario r : resumenes) {
            if (!r.isLaborable()) continue;
            w.write(String.format(Locale.ROOT,
                    "%-5d | %-9d | %-9d | %-9d | %-8d | %-12.2f | %-9.1f | %-9.1f | %-20s%n",
                    r.getNumeroDia(), r.getGenerados(), r.getAtendidosPrincipal(),
                    r.getAtendidosRezagados(), r.getTotalAtendidos(), r.getMontoTotal(),
                    r.getPromedioEspera(), r.getPromedioAtencion(),
                    r.getCajeroEstrella() != null ? r.getCajeroEstrella() : "-"));
        }
        w.write(SUBLINEA + "\n");
    }

    private void escribirResumenFinal(FileWriter w, EstadisticasFinancierasService est) throws IOException {
        w.write("RESUMEN FINAL\n");
        w.write(SUBLINEA + "\n");

        w.write("  FASE PRINCIPAL (acumulado)\n");
        w.write(String.format(Locale.ROOT, "    Atendidos : %d%n", est.getAcumAtendPpal()));
        w.write(String.format(Locale.ROOT, "    Monto     : Bs %.2f%n", est.getAcumMontoPpal()));
        w.write(String.format(Locale.ROOT, "    Espera    : %.1f min%n", est.getPrincipal().getPromedioEspera()));
        w.write(String.format(Locale.ROOT, "    Atencion  : %.1f min%n", est.getPrincipal().getPromedioAtencion()));
        w.write(String.format("    Cajero    : %s%n%n", est.getPrincipal().getCajeroEstrella()));

        w.write("  FASE REZAGADOS (acumulado)\n");
        if (est.getAcumAtendRez() == 0) {
            w.write("    Sin rezagados\n\n");
        } else {
            w.write(String.format(Locale.ROOT, "    Atendidos : %d%n", est.getAcumAtendRez()));
            w.write(String.format(Locale.ROOT, "    Monto     : Bs %.2f%n", est.getAcumMontoRez()));
            w.write(String.format(Locale.ROOT, "    Espera    : %.1f min%n", est.getRezagados().getPromedioEspera()));
            w.write(String.format(Locale.ROOT, "    Atencion  : %.1f min%n", est.getRezagados().getPromedioAtencion()));
            w.write(String.format("    Cajero    : %s%n%n", est.getRezagados().getCajeroEstrella()));
        }

        w.write("  TOTAL GLOBAL\n");
        w.write(String.format(Locale.ROOT, "    Dias laborables : %d%n", est.getDiasAcumulados()));
        w.write(String.format(Locale.ROOT, "    Generados       : %d%n", est.getAcumGenerados()));
        w.write(String.format(Locale.ROOT, "    Atendidos       : %d%n", est.getAcumTotalAtendidos()));
        w.write(String.format(Locale.ROOT, "    No atendidos    : %d%n", est.getAcumNoAtendidos()));
        w.write(String.format(Locale.ROOT, "    Monto total     : Bs %.2f%n", est.getAcumMonto()));
        w.write(String.format(Locale.ROOT, "    Espera promedio : %.1f min%n", est.getAcumPromedioEspera()));
        w.write(String.format(Locale.ROOT, "    Atencion prom.  : %.1f min%n", est.getAcumPromedioAtencion()));
        w.write(String.format("    Cajero estrella : %s%n", est.getCajeroEstrellaGlobal()));
        w.write(SUBLINEA + "\n");
    }

    private void escribirEstadisticasAcumuladas(FileWriter w, EstadisticasFinancierasService est,
                                                 long minutosSimulados) throws IOException {
        w.write("ESTADISTICAS ACUMULADAS\n");
        w.write(SUBLINEA + "\n");
        w.write(String.format(Locale.ROOT, "  Monto Principal  : Bs %.2f%n", est.getAcumMontoPpal()));
        w.write(String.format(Locale.ROOT, "  Monto Rezagados  : Bs %.2f%n", est.getAcumMontoRez()));
        if (minutosSimulados > 0) {
            w.write(String.format(Locale.ROOT, "  Eficiencia global: %.3f atendidos/min%n",
                    est.getEficienciaGlobal(minutosSimulados)));
        }
        w.write(SUBLINEA + "\n");
    }

    private String centrar(String texto) {
        int espacio = Math.max(0, (78 - texto.length()) / 2);
        return " ".repeat(espacio) + texto;
    }
}