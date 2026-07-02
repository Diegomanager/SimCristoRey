package com.supermercado.presentation.cooperativa.panel;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.EstadoCaja;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVentanillas extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<Caja> cajas;

    public PanelVentanillas() {
        setBorder(BorderFactory.createTitledBorder("Ventanillas"));
        setLayout(new BorderLayout(5, 5));

        String[] columnas = {"Caja", "Tipo", "Estado", "Socio Actual", "Ficha", "Servicio", "Monto (Bs)", "Atendidos"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public void setCajas(List<Caja> cajas) {
        this.cajas = cajas;
    }

    public void actualizar(List<Caja> cajas) {
        this.cajas = cajas;
        modelo.setRowCount(0);
        if (cajas == null) return;
        for (Caja c : cajas) {
            String socioInfo = c.getSocioActual() != null ? c.getSocioActual().getId() : "Libre";
            String ficha = c.getSocioActual() != null ? c.getSocioActual().getFicha() : "-";
            String servicio = c.getSocioActual() != null && c.getSocioActual().getServicio() != null
                    ? c.getSocioActual().getServicio().getNombre() : "-";
            String monto = c.getSocioActual() != null ? String.format("%.2f", c.getSocioActual().getMonto()) : "-";
            modelo.addRow(new Object[]{
                c.getId(),
                c.getTipo() != null ? c.getTipo().getNombre() : "-",
                c.getEstado() != null ? c.getEstado().name() : "-",
                socioInfo,
                ficha,
                servicio,
                monto,
                c.getTotalAtendidos()
            });
        }
    }

    public void limpiar() {
        modelo.setRowCount(0);
    }
}