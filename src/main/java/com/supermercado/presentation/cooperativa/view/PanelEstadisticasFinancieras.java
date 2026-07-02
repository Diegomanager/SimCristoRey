package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class PanelEstadisticasFinancieras extends JPanel {

    private final JLabel lblAtendidos       = etiqueta("0");
    private final JLabel lblGenerados       = etiqueta("0");
    private final JLabel lblEspera          = etiqueta("0.0 min");
    private final JLabel lblAtencion        = etiqueta("0.0 min");
    private final JLabel lblMonto           = etiqueta("Bs 0.00");
    private final JLabel lblCajeroEstrella  = etiqueta("N/A");
    private final JLabel lblEficiencia      = etiqueta("0.00");
    private final JTextArea areaServicios   = new JTextArea(6, 20);

    private final NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es","BO"));

    public PanelEstadisticasFinancieras() {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Estadisticas Financieras",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));

        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        JPanel grid = new JPanel(new GridLayout(0, 2, 6, 4));
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        grid.add(label("Total generados:"));   grid.add(lblGenerados);
        grid.add(label("Total atendidos:"));   grid.add(lblAtendidos);
        grid.add(label("Espera promedio:"));   grid.add(lblEspera);
        grid.add(label("Atencion promedio:")); grid.add(lblAtencion);
        grid.add(label("Monto total:"));       grid.add(lblMonto);
        grid.add(new JSeparator());            grid.add(new JSeparator());
        grid.add(label("Cajero Estrella:"));   grid.add(lblCajeroEstrella);
        grid.add(label("Eficiencia:"));        grid.add(lblEficiencia);

        areaServicios.setEditable(false);
        areaServicios.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        areaServicios.setBackground(new Color(245, 245, 250));
        JScrollPane scrollServicios = new JScrollPane(areaServicios);
        scrollServicios.setBorder(BorderFactory.createTitledBorder("Por servicio"));

        add(grid,            BorderLayout.NORTH);
        add(scrollServicios, BorderLayout.CENTER);
    }

    public void actualizar(EstadisticasFinancierasService est, int totalGenerados) {
        if (est == null) return;
        SwingUtilities.invokeLater(() -> {
            lblGenerados.setText(String.valueOf(totalGenerados));
            lblAtendidos.setText(String.valueOf(est.getTotalAtendidos()));
            lblEspera.setText(String.format("%.1f min", est.getPromedioEspera()));
            lblAtencion.setText(String.format("%.1f min", est.getPromedioAtencion()));
            lblMonto.setText("Bs " + nf.format(est.getMontoTotal()));

            String estrella = est.getCajeroEstrella();
            lblCajeroEstrella.setText(estrella);
            lblEficiencia.setText(String.format("%.2f", est.getEficienciaCajero(estrella)));

            Map<String, Integer> porServicio = est.getAtendidosPorServicio();
            StringBuilder sb = new StringBuilder();
            porServicio.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> sb.append(String.format("%-22s %4d\n", e.getKey(), e.getValue())));
            areaServicios.setText(sb.toString());
        });
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(0, 80, 160));
        return l;
    }

    private static JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }
}