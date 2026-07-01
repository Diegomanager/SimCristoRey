package com.supermercado.presentation.supermercado.view;

import com.supermercado.application.supermercado.usecase.TeoriaColasUseCase;
import com.supermercado.domain.supermercado.model.Configuracion;
import com.supermercado.domain.supermercado.model.Recomendacion;

import javax.swing.*;
import java.awt.*;

public class PanelTeoriaColas extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextArea txtMetricas;
    private JTextArea txtRecomendacion;
    private JButton btnCalcular;
    private TeoriaColasUseCase teoriaColasUseCase;
    
    public PanelTeoriaColas() {
        this.teoriaColasUseCase = new TeoriaColasUseCase();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Teoria de Colas - Analisis Avanzado"));
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        
        txtMetricas = new JTextArea();
        txtMetricas.setEditable(false);
        txtMetricas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtMetricas.setBackground(new Color(240, 240, 245));
        txtMetricas.setText("Presione 'Calcular' para obtener metricas de teoria de colas.");
        JScrollPane scrollMetricas = new JScrollPane(txtMetricas);
        scrollMetricas.setBorder(BorderFactory.createTitledBorder("Metricas M/M/c"));
        panelCentral.add(scrollMetricas);
        
        txtRecomendacion = new JTextArea();
        txtRecomendacion.setEditable(false);
        txtRecomendacion.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtRecomendacion.setBackground(new Color(255, 250, 240));
        txtRecomendacion.setText("Las recomendaciones basadas en teoria de colas apareceran aqui.");
        JScrollPane scrollRecomendacion = new JScrollPane(txtRecomendacion);
        scrollRecomendacion.setBorder(BorderFactory.createTitledBorder("Recomendacion"));
        panelCentral.add(scrollRecomendacion);
        
        add(panelCentral, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnCalcular = new JButton("Calcular Metricas");
        btnCalcular.addActionListener(e -> calcularMetricas());
        panelInferior.add(btnCalcular);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    public void calcularMetricas(Configuracion configuracion) {
        if (configuracion == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay configuracion disponible.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String metricas = teoriaColasUseCase.ejecutar(configuracion);
        txtMetricas.setText(metricas);
        
        Recomendacion recomendacion = teoriaColasUseCase.obtenerRecomendacion(configuracion);
        txtRecomendacion.setText(recomendacion.getMensaje());
    }
    
    private void calcularMetricas() {
        JOptionPane.showMessageDialog(this,
            "Primero establezca una configuracion desde el panel de simulacion.",
            "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void limpiar() {
        txtMetricas.setText("Presione 'Calcular' para obtener metricas.");
        txtRecomendacion.setText("Las recomendaciones apareceran aqui.");
    }
}