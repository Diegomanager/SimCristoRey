package com.supermercado.presentation.cooperativa.view;

import javax.swing.*;

public class MainCooperativa {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimuladorCooperativaFrame frame = new SimuladorCooperativaFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}