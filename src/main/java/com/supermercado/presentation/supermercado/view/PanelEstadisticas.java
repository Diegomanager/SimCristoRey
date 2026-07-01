package com.supermercado.presentation.supermercado.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelEstadisticas extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel lblHora;
    private JLabel lblAtendidos;
    private JLabel lblEnCola;
    private JLabel lblGenerados;
    private JLabel lblArticulos;
    private JLabel lblPromedio;
    private JLabel lblCajeroEstrella;

    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_VALOR = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_SEC   = new Font("Segoe UI", Font.BOLD,  11);
    private static final Color C_LABEL = new Color(80,  80,  80);
    private static final Color C_VALOR = new Color(25,  25,  25);
    private static final Color C_SEC   = new Color(41,  98, 155);

    public PanelEstadisticas() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        construir();
    }

    private void construir() {
        add(crearEncabezadoSeccion("Tiempo"));
        add(crearFila("Hora simulada:", lblHora = crearValor("08:00")));
        add(Box.createVerticalStrut(10));

        add(crearEncabezadoSeccion("Clientes"));
        add(crearFila("Atendidos:",     lblAtendidos = crearValor("0")));
        add(crearFila("En cola ahora:", lblEnCola    = crearValor("0")));
        add(crearFila("Generados:",     lblGenerados = crearValor("0")));
        add(Box.createVerticalStrut(10));

        add(crearEncabezadoSeccion("Ventas"));
        add(crearFila("Articulos vendidos:", lblArticulos = crearValor("0")));
        add(crearFila("Prom. atencion:",     lblPromedio  = crearValor("0.0 min")));
        add(Box.createVerticalStrut(10));

        add(crearEncabezadoSeccion("Rendimiento"));
        add(crearFila("Cajero estrella:", lblCajeroEstrella = crearValor("-")));
    }

    private JPanel crearEncabezadoSeccion(String texto) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        p.setBorder(BorderFactory.createEmptyBorder(2, 0, 4, 0));
        JLabel lbl = new JLabel(texto.toUpperCase());
        lbl.setFont(F_SEC);
        lbl.setForeground(C_SEC);
        p.add(lbl, BorderLayout.WEST);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(210, 218, 230));
        p.add(sep, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearFila(String labelTxt, JLabel valorLbl) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        fila.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JLabel lbl = new JLabel(labelTxt);
        lbl.setFont(F_LABEL);
        lbl.setForeground(C_LABEL);
        fila.add(lbl,     BorderLayout.WEST);
        fila.add(valorLbl, BorderLayout.EAST);
        return fila;
    }

    private JLabel crearValor(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(F_VALOR);
        lbl.setForeground(C_VALOR);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    /**
     * Normaliza una cadena de hora "HH:mm" para que nunca supere 23:59.
     * Si el servicio manda "31:57" la convierte a "07:57" (31 mod 24 = 7).
     */
    private String normalizarHora(String hora) {
        if (hora == null || hora.isEmpty()) return "08:00";
        try {
            String[] partes = hora.split(":");
            if (partes.length != 2) return hora;
            int h = Integer.parseInt(partes[0]);
            int m = Integer.parseInt(partes[1]);
            h = h % 24;
            return String.format("%02d:%02d", h, m);
        } catch (NumberFormatException e) {
            return hora;
        }
    }

    public void actualizarEstadisticasAvanzadas(
            String hora, int atendidos, int articulos, int enCola,
            int generados, double promedio, String cajeroEstrella) {

        SwingUtilities.invokeLater(() -> {
            lblHora.setText(normalizarHora(hora));
            lblAtendidos.setText(String.valueOf(atendidos));
            lblArticulos.setText(String.valueOf(articulos));
            lblEnCola.setText(String.valueOf(enCola));
            lblGenerados.setText(String.valueOf(generados));
            lblPromedio.setText(String.format("%.1f min", promedio));
            lblCajeroEstrella.setText(cajeroEstrella != null ? cajeroEstrella : "-");

            lblEnCola.setForeground(enCola > 20 ? Theme.ALERT_RED
                : enCola > 10 ? Theme.WARNING_YELLOW
                : new Color(25, 25, 25));

            revalidate(); repaint();
            if (getParent() != null) {
                getParent().revalidate();
                getParent().repaint();
            }
        });
    }

    public void reiniciar() {
        actualizarEstadisticasAvanzadas("08:00", 0, 0, 0, 0, 0.0, "-");
    }
}