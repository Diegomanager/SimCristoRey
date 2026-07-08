package com.supermercado.presentation.cooperativa.view;

import javax.swing.*;

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