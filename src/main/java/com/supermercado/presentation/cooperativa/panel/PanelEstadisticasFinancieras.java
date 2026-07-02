package com.supermercado.presentation.cooperativa.panel;

import com.supermercado.application.cooperativa.dto.EstadoSimulacionDTO;

import javax.swing.*;
import java.awt.*;

public class PanelEstadisticasFinancieras extends JPanel {
    private JLabel lblMontoTotal;
    private JLabel lblIntereses;
    private JLabel lblTiempoPromedio;

    public PanelEstadisticasFinancieras() {
        setBorder(BorderFactory.createTitledBorder("Estadísticas Financieras"));
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Monto total atendido:"));
        lblMontoTotal = new JLabel("Bs 0.00");
        add(lblMontoTotal);

        add(new JLabel("Intereses generados:"));
        lblIntereses = new JLabel("Bs 0.00");
        add(lblIntereses);

        add(new JLabel("Tiempo promedio atención:"));
        lblTiempoPromedio = new JLabel("0 min");
        add(lblTiempoPromedio);
    }

    public void actualizar(EstadoSimulacionDTO estado) {
        if (estado == null) return;
        // Por ahora solo mostramos valores por defecto
        // Más adelante se conectará con estadísticas reales
    }
}