package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.event.EventoSimulacion;
import com.supermercado.domain.cooperativa.event.TipoEvento;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PanelLog extends JPanel {

    private final List<EventoSimulacion> historial = new ArrayList<>();
    private final JTextPane  pane = new JTextPane();
    private final StyledDocument doc;
    private final JLabel lblTotal = new JLabel("Eventos: 0");

    private static final Map<TipoEvento, Color> COLORES = new EnumMap<>(TipoEvento.class);
    static {
        COLORES.put(TipoEvento.SOCIO_GENERADO,              new Color(30,100,200));
        COLORES.put(TipoEvento.SOCIO_ASIGNADO,              new Color(0,140,60));
        COLORES.put(TipoEvento.SOCIO_ATENDIDO,              new Color(0,170,80));
        COLORES.put(TipoEvento.SIMULACION_FINALIZADA,       new Color(200,0,0));
        COLORES.put(TipoEvento.FASE_REZAGADOS_INICIADA,     new Color(180,100,0));
        COLORES.put(TipoEvento.FASE_PRINCIPAL_FINALIZADA,   new Color(0,0,200));
        COLORES.put(TipoEvento.DIA_INICIADO,                new Color(120,0,180));
        COLORES.put(TipoEvento.DIA_FINALIZADO,              new Color(150,0,200));
        COLORES.put(TipoEvento.SIMULACION_MENSUAL_FINALIZADA, new Color(200,0,100));
        COLORES.put(TipoEvento.SIMULACION_PAUSADA,          Color.DARK_GRAY);
        COLORES.put(TipoEvento.SIMULACION_DETENIDA,         Color.DARK_GRAY);
        COLORES.put(TipoEvento.SIMULACION_INICIADA,         new Color(0,80,160));
        COLORES.put(TipoEvento.SIMULACION_REANUDADA,        new Color(0,80,160));
        COLORES.put(TipoEvento.SIMULACION_REINICIADA,       new Color(100,0,150));
    }

    public PanelLog() {
        setLayout(new BorderLayout(4,4));
        setBorder(BorderFactory.createTitledBorder("\uD83D\uDCCB Log de Eventos (Hora Simulada)"));

        pane.setEditable(false);
        pane.setBackground(new Color(18,20,30));
        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        doc = pane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(pane);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setBlockIncrement(80);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton btnLimpiar  = new JButton("Limpiar");
        JButton btnVerTodo  = new JButton("\u2193 Ir al final");
        JButton btnIrInicio = new JButton("\u2191 Ir al inicio");
        btnLimpiar.addActionListener(e -> limpiar());
        btnVerTodo.addActionListener(e -> pane.setCaretPosition(doc.getLength()));
        btnIrInicio.addActionListener(e -> pane.setCaretPosition(0));

        lblTotal.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        top.add(lblTotal);
        top.add(btnIrInicio);
        top.add(btnVerTodo);
        top.add(btnLimpiar);

        add(top,    BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void addEvento(EventoSimulacion ev) {
        synchronized (historial) { historial.add(ev); }

        SwingUtilities.invokeLater(() -> {
            try {
                Color color = COLORES.getOrDefault(ev.getTipo(), Color.LIGHT_GRAY);
                SimpleAttributeSet a = new SimpleAttributeSet();
                StyleConstants.setForeground(a, color);
                StyleConstants.setFontFamily(a, Font.MONOSPACED);
                StyleConstants.setFontSize(a, 11);

                boolean bold = switch (ev.getTipo()) {
                    case SIMULACION_FINALIZADA, FASE_PRINCIPAL_FINALIZADA,
                         FASE_REZAGADOS_INICIADA, DIA_FINALIZADO,
                         SIMULACION_MENSUAL_FINALIZADA, DIA_INICIADO -> true;
                    default -> false;
                };
                StyleConstants.setBold(a, bold);

                doc.insertString(doc.getLength(), ev.toString() + "\n", a);

                int total;
                synchronized (historial) { total = historial.size(); }
                lblTotal.setText("Eventos: " + total);

                JScrollBar bar = ((JScrollPane) getComponent(1)).getVerticalScrollBar();
                if (bar.getValue() >= bar.getMaximum() - bar.getVisibleAmount() - 40) {
                    pane.setCaretPosition(doc.getLength());
                }
            } catch (BadLocationException ignored) {}
        });
    }

    public void limpiar() {
        SwingUtilities.invokeLater(() -> {
            try {
                doc.remove(0, doc.getLength());
                synchronized (historial) { historial.clear(); }
                lblTotal.setText("Eventos: 0");
            } catch (BadLocationException ignored) {}
        });
    }

    public int getTotalEventos() {
        synchronized (historial) { return historial.size(); }
    }
}