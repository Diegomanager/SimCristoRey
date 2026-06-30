package com.supermercado.presentation.view;

import com.supermercado.application.dto.DiagnosticoDTO;
import com.supermercado.application.usecase.DiagnosticarConfiguracionUseCase;
import com.supermercado.domain.model.Caja;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class PanelDiagnostico extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTable tablaDiagnostico;
    private JTextArea txtResumen;
    private JButton btnActualizar;
    private DiagnosticarConfiguracionUseCase diagnosticarUseCase;
    private List<Caja> cajasReferencia;
    
    private static final Color COLOR_VERDE = new Color(46, 204, 113);
    private static final Color COLOR_AMARILLO = new Color(241, 196, 15);
    private static final Color COLOR_NARANJA = new Color(243, 156, 18);
    private static final Color COLOR_ROJO = new Color(231, 76, 60);
    
    public PanelDiagnostico() {
        this.diagnosticarUseCase = new DiagnosticarConfiguracionUseCase();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Diagnostico del Sistema"));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        String[] columnas = {"Caja", "Nivel", "Estado", "Mensaje", "Sugerencia"};
        tablaDiagnostico = new JTable(new DefaultTableModel(columnas, 0)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDiagnostico.setRowHeight(30);
        tablaDiagnostico.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroll = new JScrollPane(tablaDiagnostico);
        scroll.setPreferredSize(new Dimension(0, 200));
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        
        txtResumen = new JTextArea(4, 40);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResumen.setBackground(new Color(245, 245, 250));
        panelInferior.add(new JScrollPane(txtResumen), BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnActualizar = new JButton("Actualizar Diagnostico");
        btnActualizar.addActionListener(e -> actualizarDiagnostico());
        panelBotones.add(btnActualizar);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    public void setCajas(List<Caja> cajas) {
        this.cajasReferencia = cajas;
        actualizarDiagnostico();
    }
    
    public void actualizarDiagnostico() {
        if (cajasReferencia == null || cajasReferencia.isEmpty()) {
            txtResumen.setText("No hay datos de cajas disponibles. Ejecute una simulacion primero.");
            return;
        }
        
        DiagnosticoDTO diagnostico = diagnosticarUseCase.ejecutar(cajasReferencia);
        
        DefaultTableModel model = (DefaultTableModel) tablaDiagnostico.getModel();
        model.setRowCount(0);
        
        for (DiagnosticoDTO.DiagnosticoItem item : diagnostico.getItems()) {
            String nivelDisplay = item.getIcono() + " " + item.getNivel();
            String estadoDisplay = item.getColaActual() + "/" + item.getColaMaxima();
            
            model.addRow(new Object[]{
                item.getCajaId(),
                nivelDisplay,
                estadoDisplay,
                item.getMensaje(),
                item.getSugerencia()
            });
        }
        
        // Aplicar colores a las filas
        tablaDiagnostico.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String nivel = table.getValueAt(row, 1).toString();
                Color bgColor = Color.WHITE;
                
                if (nivel.contains("CRITICO")) {
                    bgColor = COLOR_ROJO;
                    c.setForeground(Color.WHITE);
                } else if (nivel.contains("ALERTA")) {
                    bgColor = COLOR_NARANJA;
                    c.setForeground(Color.WHITE);
                } else if (nivel.contains("ATENCION")) {
                    bgColor = COLOR_AMARILLO;
                    c.setForeground(Color.BLACK);
                } else {
                    bgColor = COLOR_VERDE;
                    c.setForeground(Color.WHITE);
                }
                
                if (!isSelected) {
                    c.setBackground(bgColor);
                }
                return c;
            }
        });
        
        txtResumen.setText(diagnostico.getResumenEjecutivo());
    }
    
    public void limpiar() {
        DefaultTableModel model = (DefaultTableModel) tablaDiagnostico.getModel();
        model.setRowCount(0);
        txtResumen.setText("");
    }
}