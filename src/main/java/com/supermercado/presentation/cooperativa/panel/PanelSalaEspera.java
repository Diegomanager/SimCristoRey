package com.supermercado.presentation.cooperativa.panel;

import com.supermercado.domain.cooperativa.model.SalaEspera;
import com.supermercado.domain.cooperativa.model.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelSalaEspera extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblTotal;
    private JLabel lblAtendidos;

    public PanelSalaEspera() {
        setBorder(BorderFactory.createTitledBorder("Sala de Espera"));
        setLayout(new BorderLayout(5, 5));

        JPanel contadores = new JPanel(new GridLayout(1, 2));
        lblTotal = new JLabel("En espera: 0");
        lblAtendidos = new JLabel("Atendidos: 0");
        contadores.add(lblTotal);
        contadores.add(lblAtendidos);
        add(contadores, BorderLayout.NORTH);

        String[] columnas = {"Ficha", "Servicio", "Monto (Bs)", "Preferente"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public void actualizar(SalaEspera sala) {
        if (sala == null) return;
        lblTotal.setText("En espera: " + sala.getTotalEsperando());
        lblAtendidos.setText("Atendidos: " + sala.getTotalAtendidos());
        modelo.setRowCount(0);
        for (Socio s : sala.getSociosEnEspera()) {
            modelo.addRow(new Object[]{
                s.getFicha(),
                s.getServicio() != null ? s.getServicio().getNombre() : "General",
                String.format("%.2f", s.getMonto()),
                s.isEsPreferente() ? "Sí" : "No"
            });
        }
    }

    public void limpiar() {
        modelo.setRowCount(0);
    }
}