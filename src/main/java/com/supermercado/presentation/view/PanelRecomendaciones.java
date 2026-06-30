package com.supermercado.presentation.view;

import com.supermercado.application.dto.RecomendacionDTO;
import com.supermercado.application.usecase.RecomendarMejoraUseCase;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Configuracion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelRecomendaciones extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextArea txtRecomendacion;
    private JButton btnAnalizar;
    private JButton btnAceptar;
    private JButton btnRechazar;
    private RecomendarMejoraUseCase recomendarUseCase;
    private List<Caja> cajasReferencia;
    private Configuracion configuracionReferencia;
    private RecomendacionDTO recomendacionActual;
    
    private static final Color COLOR_CRITICO = new Color(231, 76, 60);
    private static final Color COLOR_ALTO = new Color(243, 156, 18);
    private static final Color COLOR_NORMAL = new Color(46, 204, 113);
    
    public PanelRecomendaciones() {
        this.recomendarUseCase = new RecomendarMejoraUseCase();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Recomendaciones de Mejora"));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        
        txtRecomendacion = new JTextArea();
        txtRecomendacion.setEditable(false);
        txtRecomendacion.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtRecomendacion.setBackground(new Color(250, 250, 255));
        txtRecomendacion.setText("Presione 'Analizar' para obtener recomendaciones basadas en la simulacion actual.");
        JScrollPane scroll = new JScrollPane(txtRecomendacion);
        scroll.setPreferredSize(new Dimension(0, 200));
        panelCentral.add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        btnAnalizar = new JButton("Analizar y Recomendar");
        btnAnalizar.addActionListener(e -> analizarYRecomendar());
        panelBotones.add(btnAnalizar);
        
        btnAceptar = new JButton("Aceptar Recomendacion");
        btnAceptar.setEnabled(false);
        btnAceptar.addActionListener(e -> aceptarRecomendacion());
        panelBotones.add(btnAceptar);
        
        btnRechazar = new JButton("Rechazar");
        btnRechazar.setEnabled(false);
        btnRechazar.addActionListener(e -> rechazarRecomendacion());
        panelBotones.add(btnRechazar);
        
        panelCentral.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelCentral, BorderLayout.CENTER);
    }
    
    public void setDatos(List<Caja> cajas, Configuracion configuracion) {
        this.cajasReferencia = cajas;
        this.configuracionReferencia = configuracion;
        btnAnalizar.setEnabled(cajas != null && !cajas.isEmpty());
    }
    
    private void analizarYRecomendar() {
        if (cajasReferencia == null || cajasReferencia.isEmpty()) {
            txtRecomendacion.setText("No hay datos de cajas disponibles. Ejecute una simulacion primero.");
            return;
        }
        
        recomendacionActual = recomendarUseCase.ejecutar(cajasReferencia, configuracionReferencia);
        
        String prioridad = recomendacionActual.getPrioridad();
        String mensaje = recomendacionActual.getMensaje();
        
        if ("CRITICO".equals(prioridad)) {
            txtRecomendacion.setBackground(COLOR_CRITICO);
            txtRecomendacion.setForeground(Color.WHITE);
        } else if ("ALTO".equals(prioridad)) {
            txtRecomendacion.setBackground(COLOR_ALTO);
            txtRecomendacion.setForeground(Color.BLACK);
        } else {
            txtRecomendacion.setBackground(COLOR_NORMAL);
            txtRecomendacion.setForeground(Color.BLACK);
        }
        
        txtRecomendacion.setText(mensaje);
        
        btnAceptar.setEnabled(true);
        btnRechazar.setEnabled(true);
    }
    
    private void aceptarRecomendacion() {
        if (recomendacionActual != null) {
            JOptionPane.showMessageDialog(this,
                "Recomendacion aceptada.\n" +
                "Configuracion sugerida: " + recomendacionActual.getConfiguracionSugerida() + "\n" +
                "Mejora estimada: " + String.format("%.1f", recomendacionActual.getMejoraEstimada()) + "%",
                "Recomendacion Aceptada",
                JOptionPane.INFORMATION_MESSAGE);
            
            btnAceptar.setEnabled(false);
            btnRechazar.setEnabled(false);
        }
    }
    
    private void rechazarRecomendacion() {
        if (recomendacionActual != null) {
            JOptionPane.showMessageDialog(this,
                "Recomendacion rechazada.\n" +
                "Manteniendo configuracion actual.",
                "Recomendacion Rechazada",
                JOptionPane.INFORMATION_MESSAGE);
            
            btnAceptar.setEnabled(false);
            btnRechazar.setEnabled(false);
        }
    }
    
    public void limpiar() {
        txtRecomendacion.setText("Presione 'Analizar' para obtener recomendaciones.");
        txtRecomendacion.setBackground(new Color(250, 250, 255));
        txtRecomendacion.setForeground(Color.BLACK);
        btnAceptar.setEnabled(false);
        btnRechazar.setEnabled(false);
        recomendacionActual = null;
    }
}