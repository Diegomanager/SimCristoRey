package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.event.EventoSimulacion;
import com.supermercado.domain.cooperativa.event.TipoEvento;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class PanelLog extends JPanel {

    private static final int MAX_LINEAS = 300;

    private final JTextPane  textPane   = new JTextPane();
    private final StyledDocument doc;

    private static final Map<TipoEvento, Color> COLORES = new EnumMap<>(TipoEvento.class);
    static {
        COLORES.put(TipoEvento.SOCIO_GENERADO,          new Color(0, 120, 215));
        COLORES.put(TipoEvento.SOCIO_ASIGNADO,          new Color(16, 124, 16));
        COLORES.put(TipoEvento.SOCIO_ATENDIDO,          new Color(0, 153, 76));
        COLORES.put(TipoEvento.SIMULACION_FINALIZADA,   new Color(200, 0, 0));
        COLORES.put(TipoEvento.FASE_REZAGADOS_INICIADA, new Color(180, 100, 0));
        COLORES.put(TipoEvento.SIMULACION_PAUSADA,      Color.DARK_GRAY);
        COLORES.put(TipoEvento.SIMULACION_DETENIDA,     Color.DARK_GRAY);
        COLORES.put(TipoEvento.SIMULACION_INICIADA,     new Color(0, 80, 160));
        COLORES.put(TipoEvento.SIMULACION_REANUDADA,    new Color(0, 80, 160));
        COLORES.put(TipoEvento.SIMULACION_REINICIADA,   new Color(100, 0, 150));
    }

    public PanelLog() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Log de Eventos"));

        textPane.setEditable(false);
        textPane.setBackground(new Color(20, 20, 30));
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        doc = textPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        top.add(new JLabel("Últimos " + MAX_LINEAS + " eventos"));
        top.add(btnLimpiar);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void addEvento(EventoSimulacion evento) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (doc.getDefaultRootElement().getElementCount() > MAX_LINEAS) {
                    int fin = doc.getDefaultRootElement()
                            .getElement(MAX_LINEAS / 3).getEndOffset();
                    doc.remove(0, fin);
                }

                Color color = COLORES.getOrDefault(evento.getTipo(), Color.LIGHT_GRAY);
                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, color);
                StyleConstants.setFontFamily(attr, Font.MONOSPACED);
                StyleConstants.setFontSize(attr, 11);

                if (evento.getTipo() == TipoEvento.SIMULACION_FINALIZADA
                        || evento.getTipo() == TipoEvento.FASE_REZAGADOS_INICIADA) {
                    StyleConstants.setBold(attr, true);
                }

                doc.insertString(doc.getLength(), evento.toString() + "\n", attr);
                textPane.setCaretPosition(doc.getLength());

            } catch (BadLocationException ignored) {}
        });
    }

    public void limpiar() {
        SwingUtilities.invokeLater(() -> {
            try { doc.remove(0, doc.getLength()); } catch (BadLocationException ignored) {}
        });
    }
}