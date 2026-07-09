package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.model.ResumenDiario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class PanelEvolucionDiaria extends JPanel {

    private static final String[] COLS = {
        "Dia", "Laborable", "Generados", "P.Principal", "Rezagados",
        "Total", "No Atend.", "Monto (Bs)", "Espera(min)",
        "Aten.(min)", "Cajero"
    };

    private final DefaultTableModel modelo;
    private final JTable            tabla;
    private final JScrollPane       scroll;
    private final List<ResumenDiario> resumenes = new ArrayList<>();
    private boolean tieneTotales = false;

    public PanelEvolucionDiaria() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Evolucion Diaria"));

        modelo = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch(c) {
                    case 0,2,3,4,5,6 -> Integer.class;
                    case 7,8,9       -> Double.class;
                    default          -> String.class;
                };
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setFillsViewportHeight(true);

        int[] anchos = {40, 70, 72, 85, 75, 65, 72, 105, 85, 80, 90};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                Object lab = modelo.getValueAt(row, 1);
                if ("No".equals(lab)) {
                    setBackground(new Color(240, 240, 240));
                    setForeground(Color.GRAY);
                } else if ("TOTAL".equals(lab)) {
                    setBackground(new Color(200, 230, 255));
                    setForeground(new Color(0, 60, 120));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (sel) {
                    setBackground(new Color(173, 216, 230));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                    setForeground(Color.BLACK);
                }
                if (v instanceof Number) setHorizontalAlignment(RIGHT);
                else setHorizontalAlignment(LEFT);
                return this;
            }
        });

        scroll = new JScrollPane(tabla,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (!resumenes.isEmpty()) reconstruirTabla();
            }
        });
    }

    public void addResumenDia(ResumenDiario r) {
        if (r == null || !r.isLaborable()) return;
        resumenes.add(r);
        SwingUtilities.invokeLater(() -> {
            agregarFilaModelo(r);
            repintarTodo();
        });
    }

    private void agregarFilaModelo(ResumenDiario r) {
        modelo.addRow(new Object[]{
            r.getNumeroDia(),
            r.isLaborable() ? "Si" : "No",
            r.getGenerados(),
            r.getAtendidosPrincipal(),
            r.getAtendidosRezagados(),
            r.getTotalAtendidos(),
            r.getNoAtendidos(),
            round2(r.getMontoTotal()),
            round1(r.getPromedioEspera()),
            round1(r.getPromedioAtencion()),
            r.getCajeroEstrella() != null ? r.getCajeroEstrella() : "-"
        });
    }

    public void mostrarTotalesYRefrescar() {
        tieneTotales = true;
        SwingUtilities.invokeLater(() -> {
            int gen=0,ppal=0,rez=0,tot=0,no=0;
            double monto=0,esp=0,aten=0;
            int cont = 0;
            for (ResumenDiario r : resumenes) {
                if (r.isLaborable()) {
                    gen  += r.getGenerados();
                    ppal += r.getAtendidosPrincipal();
                    rez  += r.getAtendidosRezagados();
                    tot  += r.getTotalAtendidos();
                    no   += r.getNoAtendidos();
                    monto+= r.getMontoTotal();
                    esp  += r.getPromedioEspera();
                    aten += r.getPromedioAtencion();
                    cont++;
                }
            }
            if (cont > 0) {
                modelo.addRow(new Object[]{
                    "-", "TOTAL", gen, ppal, rez, tot, no,
                    round2(monto),
                    round1(esp / cont),
                    round1(aten / cont),
                    "-"
                });
            }
            repintarTodo();
        });
    }

    public void reconstruirTabla() {
        SwingUtilities.invokeLater(() -> {
            modelo.setRowCount(0);
            for (ResumenDiario r : resumenes) {
                agregarFilaModelo(r);
            }
            if (tieneTotales && !resumenes.isEmpty()) {
                int gen=0,ppal=0,rez=0,tot=0,no=0;
                double monto=0,esp=0,aten=0; int cont=0;
                for (ResumenDiario r : resumenes) {
                    if (r.isLaborable()) {
                        gen+=r.getGenerados(); ppal+=r.getAtendidosPrincipal();
                        rez+=r.getAtendidosRezagados(); tot+=r.getTotalAtendidos();
                        no+=r.getNoAtendidos(); monto+=r.getMontoTotal();
                        esp+=r.getPromedioEspera(); aten+=r.getPromedioAtencion();
                        cont++;
                    }
                }
                if (cont > 0) {
                    modelo.addRow(new Object[]{
                        "-","TOTAL",gen,ppal,rez,tot,no,
                        round2(monto),round1(esp/cont),round1(aten/cont),"-"
                    });
                }
            }
            repintarTodo();
        });
    }

    private void repintarTodo() {
        try {
            modelo.fireTableDataChanged();
            tabla.revalidate();
            tabla.repaint();
            scroll.revalidate();
            scroll.repaint();

            if (isShowing()) {
                tabla.paintImmediately(tabla.getBounds());
                scroll.paintImmediately(scroll.getBounds());
            }

            tabla.updateUI();
            scroll.updateUI();
            revalidate();
            repaint();
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                modelo.fireTableDataChanged();
                revalidate();
                repaint();
            });
        }
    }

    public void mostrarTotales() { mostrarTotalesYRefrescar(); }

    public void reiniciar() {
        resumenes.clear();
        tieneTotales = false;
        SwingUtilities.invokeLater(() -> {
            modelo.setRowCount(0);
            repintarTodo();
        });
    }

    private static double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}