package com.supermercado.presentation.cooperativa.panel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PanelLog extends JPanel {
    private JTextArea textArea;

    public PanelLog() {
        setBorder(BorderFactory.createTitledBorder("Log de Simulación"));
        setLayout(new BorderLayout());

        textArea = new JTextArea(8, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);
    }

    public void agregarMensaje(String mensaje) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String linea = String.format("[%s] %s\n", timestamp, mensaje);
        // Agregar al final y hacer scroll
        textArea.append(linea);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        // Limitar líneas para evitar sobrecarga (opcional)
        if (textArea.getLineCount() > 1000) {
            try {
                int pos = textArea.getLineStartOffset(100);
                textArea.replaceRange("", 0, pos);
            } catch (Exception e) {
                // Ignorar
            }
        }
    }

    public void limpiar() {
        textArea.setText("");
    }
}