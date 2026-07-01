package com.supermercado.presentation.supermercado.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PanelCajas extends JPanel {

    private static final long serialVersionUID = 1L;

    private Map<Integer, JPanel>       cajaPanels;
    private Map<Integer, JLabel>       estadoLabels;
    private Map<Integer, JLabel>       colaLabels;
    private Map<Integer, JLabel>       clienteLabels;
    private Map<Integer, JProgressBar> progressBars;
    private Map<Integer, JLabel>       tipoLabels;
    private Map<Integer, JPanel>       indicadorPanels;
    private Map<Integer, Color[]>      indicadorColor;
    private int totalCajas;

    // Altura fija por caja: las cajas nunca se estiran mas de esto
    private static final int CAJA_ALTO  = 145;
    private static final int CAJA_ANCHO = 220;

    public PanelCajas() {
        setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        setBackground(Theme.BACKGROUND);
        setOpaque(true);
        cajaPanels      = new HashMap<>();
        estadoLabels    = new HashMap<>();
        colaLabels      = new HashMap<>();
        clienteLabels   = new HashMap<>();
        progressBars    = new HashMap<>();
        tipoLabels      = new HashMap<>();
        indicadorPanels = new HashMap<>();
        indicadorColor  = new HashMap<>();
        totalCajas = 0;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void inicializarCajas(int total) {
        removeAll();
        cajaPanels.clear(); estadoLabels.clear(); colaLabels.clear();
        clienteLabels.clear(); progressBars.clear(); tipoLabels.clear();
        indicadorPanels.clear(); indicadorColor.clear();
        totalCajas = total;

        for (int i = 1; i <= total; i++) {
            JPanel p = crearPanelCaja(i);
            cajaPanels.put(i, p);
            add(p);
        }
        revalidate();
        repaint();
    }

    private JPanel crearPanelCaja(int numero) {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setBackground(Color.WHITE);
        // Tamanio fijo: nunca se estira
        panel.setPreferredSize(new Dimension(CAJA_ANCHO, CAJA_ALTO));
        panel.setMinimumSize(new Dimension(CAJA_ANCHO, CAJA_ALTO));
        panel.setMaximumSize(new Dimension(CAJA_ANCHO, CAJA_ALTO));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.PRIMARY_BLUE, 2),
            BorderFactory.createEmptyBorder(0, 0, 3, 0)
        ));

        // Header
        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setBackground(Theme.PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JPanel leftH = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftH.setOpaque(false);
        JLabel lblNombre = new JLabel("CAJA " + numero);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNombre.setForeground(Color.WHITE);
        leftH.add(lblNombre);

        JLabel lblTipo = new JLabel(" NORMAL ");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTipo.setForeground(Color.WHITE);
        lblTipo.setOpaque(true);
        lblTipo.setBackground(Theme.SECONDARY_ORANGE);
        lblTipo.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
        tipoLabels.put(numero, lblTipo);
        leftH.add(lblTipo);
        header.add(leftH, BorderLayout.WEST);

        // Indicador circular
        Color[] colorRef = {new Color(160, 170, 180)};
        indicadorColor.put(numero, colorRef);
        JPanel indicador = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = colorRef[0];
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
                g2.fillOval(1, 1, 20, 20);
                g2.setColor(c);
                g2.fillOval(3, 3, 16, 16);
                g2.setColor(new Color(255, 255, 255, 130));
                g2.fillOval(5, 4, 5, 5);
                g2.dispose();
            }
        };
        indicador.setPreferredSize(new Dimension(24, 24));
        indicador.setOpaque(false);
        indicadorPanels.put(numero, indicador);
        header.add(indicador, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Cuerpo
        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(6, 8, 2, 8));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(1, 0, 1, 0);
        g.gridx = 0; g.weightx = 1.0;

        g.gridy = 0;
        JLabel lblEstado = new JLabel("Estado: LIBRE");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstado.setForeground(new Color(80, 80, 80));
        estadoLabels.put(numero, lblEstado);
        info.add(lblEstado, g);

        g.gridy = 1;
        JLabel lblCola = new JLabel("En cola: 0");
        lblCola.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCola.setForeground(new Color(70, 70, 70));
        colaLabels.put(numero, lblCola);
        info.add(lblCola, g);

        g.gridy = 2;
        JLabel lblCliente = new JLabel(" ");
        lblCliente.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblCliente.setForeground(new Color(100, 100, 100));
        clienteLabels.put(numero, lblCliente);
        info.add(lblCliente, g);

        panel.add(info, BorderLayout.CENTER);

        // Barra progreso
        JProgressBar bar = new JProgressBar(0, 20);
        bar.setStringPainted(true);
        bar.setString("0 / 20");
        bar.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        bar.setForeground(Theme.SUCCESS_GREEN);
        bar.setBackground(new Color(220, 225, 230));
        bar.setPreferredSize(new Dimension(60, 16));
        bar.setBorderPainted(false);
        progressBars.put(numero, bar);

        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.setBackground(Color.WHITE);
        barPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        barPanel.add(bar, BorderLayout.CENTER);
        panel.add(barPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void actualizarCaja(int numCaja, String estado, int cola, String clienteInfo) {
        actualizarCaja(numCaja, estado, cola, clienteInfo, "");
    }

    public void actualizarCaja(int numCaja, String estado, int cola, String clienteInfo, String tipo) {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = cajaPanels.get(numCaja);
            if (panel == null) return;

            JLabel lblEstado = estadoLabels.get(numCaja);
            Color[] cRef = indicadorColor.get(numCaja);
            if (lblEstado != null) {
                switch (estado.toUpperCase()) {
                    case "LIBRE":
                        lblEstado.setText("Estado: LIBRE");
                        lblEstado.setForeground(new Color(80, 80, 80));
                        if (cRef != null) cRef[0] = new Color(160, 170, 180); break;
                    case "ATENDIENDO":
                        lblEstado.setText("Estado: ATENDIENDO");
                        lblEstado.setForeground(Theme.SUCCESS_GREEN);
                        if (cRef != null) cRef[0] = Theme.SUCCESS_GREEN; break;
                    case "PAUSADA":
                        lblEstado.setText("Estado: PAUSADA");
                        lblEstado.setForeground(Theme.WARNING_YELLOW);
                        if (cRef != null) cRef[0] = Theme.WARNING_YELLOW; break;
                    default:
                        lblEstado.setText("Estado: " + estado);
                        lblEstado.setForeground(new Color(80, 80, 80));
                        if (cRef != null) cRef[0] = new Color(160, 170, 180);
                }
                JPanel ind = indicadorPanels.get(numCaja);
                if (ind != null) ind.repaint();
            }

            JLabel lblCola = colaLabels.get(numCaja);
            if (lblCola != null) {
                lblCola.setText("En cola: " + cola + " persona" + (cola != 1 ? "s" : ""));
                lblCola.setForeground(cola > 10 ? Theme.ALERT_RED
                    : cola > 5 ? Theme.WARNING_YELLOW : new Color(70, 70, 70));
            }

            JLabel lblCliente = clienteLabels.get(numCaja);
            if (lblCliente != null)
                lblCliente.setText((clienteInfo != null && !clienteInfo.isEmpty())
                    ? "Atendiendo: " + clienteInfo : " ");

            JProgressBar bar = progressBars.get(numCaja);
            if (bar != null) {
                bar.setValue(Math.min(cola, 20));
                bar.setString(cola + " / 20");
                bar.setForeground(cola <= 2  ? Theme.COLA_VERDE
                    : cola <= 5  ? Theme.COLA_AMARILLO
                    : cola <= 10 ? Theme.COLA_NARANJA
                    : Theme.COLA_ROJO);
            }

            JLabel lblTipo = tipoLabels.get(numCaja);
            if (lblTipo != null && tipo != null && !tipo.isEmpty()) {
                lblTipo.setText(tipo.equalsIgnoreCase("R") ? " RAPIDA " : " NORMAL ");
                lblTipo.setBackground(tipo.equalsIgnoreCase("R")
                    ? Theme.PRIMARY_DARK : Theme.SECONDARY_ORANGE);
            }

            Color border = estado.equalsIgnoreCase("ATENDIENDO") ? Theme.SUCCESS_GREEN
                : estado.equalsIgnoreCase("PAUSADA") ? Theme.WARNING_YELLOW
                : Theme.PRIMARY_BLUE;
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 2),
                BorderFactory.createEmptyBorder(0, 0, 3, 0)
            ));

            panel.revalidate();
            panel.repaint();
        });
    }

    public void reiniciar() {
        if (totalCajas > 0)
            for (int i = 1; i <= totalCajas; i++)
                actualizarCaja(i, "LIBRE", 0, "", i <= totalCajas / 3 ? "R" : "N");
    }

    // ============================================================
    // WrapLayout: FlowLayout que hace wrap automatico al redimensionar
    // Las cajas fluyen en filas y nunca se estiran
    // ============================================================
    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right;

                int width = 0, height = 0, rowWidth = 0, rowHeight = 0;
                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                            width = Math.max(width, rowWidth);
                            height += rowHeight + vgap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                width = Math.max(width, rowWidth);
                height += rowHeight + insets.top + insets.bottom + vgap * 2;
                return new Dimension(width, height);
            }
        }
    }
}