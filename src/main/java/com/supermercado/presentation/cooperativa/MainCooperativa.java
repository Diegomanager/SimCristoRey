package com.supermercado.presentation.cooperativa;

import com.supermercado.presentation.cooperativa.view.SimuladorCooperativaFrame;

import javax.swing.*;

/**
 * Clase principal para ejecutar el Simulador de Cooperativa SimCristoRey.
 * Uso: java -cp target/classes com.supermercado.presentation.cooperativa.MainCooperativa
 */
public class MainCooperativa {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SimuladorCooperativaFrame().setVisible(true);
        });
    }
}