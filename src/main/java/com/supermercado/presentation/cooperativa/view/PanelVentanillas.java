package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.EstadoCaja;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelVentanillas extends JPanel {
    private List<Caja> cajas = new ArrayList<>();
    private long tiempoActual = 0;
    private final Map<String, TarjetaCaja> tarjetas = new LinkedHashMap<>();

    public PanelVentanillas() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🖥  Ventanillas",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));
    }

    public void setCajas(List<Caja> cajas) {
        this.cajas = cajas != null ? cajas : new ArrayList<>();
        SwingUtilities.invokeLater(this::construirTarjetas);
    }

    private void construirTarjetas() {
        removeAll();
        tarjetas.clear();
        if (cajas.isEmpty()) { revalidate(); repaint(); return; }

        Map<String, List<Caja>> porTipo = new LinkedHashMap<>();
        for (Caja c : cajas) {
            String tipo = c.getTipo() != null ? c.getTipo().getId() : "?";
            porTipo.computeIfAbsent(tipo, k -> new ArrayList<>()).add(c);
        }

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        for (Map.Entry<String, List<Caja>> entry : porTipo.entrySet()) {
            JPanel grupo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            grupo.setBorder(BorderFactory.createTitledBorder(etiquetaTipo(entry.getKey())));
            for (Caja caja : entry.getValue()) {
                TarjetaCaja t = new TarjetaCaja(caja);
                tarjetas.put(caja.getId(), t);
                grupo.add(t);
            }
            contenedor.add(grupo);
        }

        add(new JScrollPane(contenedor), BorderLayout.CENTER);
        revalidate(); repaint();
    }

    public void actualizar(List<Caja> cajas, long tiempoSimulado) {
        this.cajas = cajas;
        this.tiempoActual = tiempoSimulado;
        SwingUtilities.invokeLater(() -> {
            for (Caja c : cajas) {
                TarjetaCaja t = tarjetas.get(c.getId());
                if (t != null) t.actualizar(c, tiempoSimulado);
            }
        });
    }

    public void actualizar(List<Caja> cajas) { actualizar(cajas, tiempoActual); }
    public void limpiar() { tarjetas.values().forEach(TarjetaCaja::limpiar); }

    private String etiquetaTipo(String tipoId) {
        return switch (tipoId) {
            case "CAJ" -> "🏦 Caja General";
            case "CRE" -> "💳 Créditos";
            case "AHO" -> "💰 Ahorro";
            case "REC" -> "📢 Reclamos";
            case "MM"  -> "📱 Módulo Móvil";
            default    -> tipoId;
        };
    }

    static class TarjetaCaja extends JPanel {
        private static final Color COLOR_LIBRE    = new Color(220, 255, 220);
        private static final Color COLOR_OCUPADA  = new Color(255, 245, 200);
        private static final Color COLOR_PAUSADA  = new Color(230, 230, 230);
        private static final Color COLOR_INACTIVA = new Color(200, 200, 200);

        private final JLabel lblId        = bold("---", 14);
        private final JLabel lblEstado    = bold("LIBRE", 11);
        private final JLabel lblFicha     = new JLabel("-");
        private final JLabel lblServicios = new JLabel("-");
        private final JLabel lblMonto     = new JLabel("-");
        private final JLabel lblTimer     = bold("0 min", 13);
        private final JLabel lblAtendidos = new JLabel("Atendidos: 0");
        private final JTextArea areaResumen = new JTextArea(4, 20);

        TarjetaCaja(Caja caja) {
            setLayout(new BorderLayout(4, 4));
            setPreferredSize(new Dimension(230, 230));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1, true),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));

            JPanel header = new JPanel(new BorderLayout());
            header.add(lblId, BorderLayout.WEST);
            header.add(lblEstado, BorderLayout.EAST);

            JPanel body = new JPanel(new GridLayout(0, 1, 2, 2));
            body.add(new JLabel("Ticket:"));    body.add(lblFicha);
            body.add(new JLabel("Servicio(s):")); body.add(lblServicios);
            body.add(new JLabel("Monto:"));     body.add(lblMonto);
            body.add(new JLabel("⏱ En atención:")); body.add(lblTimer);
            body.add(lblAtendidos);

            areaResumen.setEditable(false);
            areaResumen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            areaResumen.setBackground(new Color(248, 248, 255));
            JScrollPane scroll = new JScrollPane(areaResumen);
            scroll.setBorder(BorderFactory.createTitledBorder("Tiempo por servicio"));
            scroll.setPreferredSize(new Dimension(210, 80));

            add(header, BorderLayout.NORTH);
            add(body,   BorderLayout.CENTER);
            add(scroll, BorderLayout.SOUTH);

            actualizar(caja, 0);
        }

        void actualizar(Caja caja, long tiempoSimulado) {
            lblId.setText(caja.getId());
            EstadoCaja estado = caja.getEstado();
            switch (estado) {
                case LIBRE       -> { setBackground(COLOR_LIBRE);    lblEstado.setText("🟢 LIBRE");    lblEstado.setForeground(new Color(0,120,0)); }
                case OCUPADA     -> { setBackground(COLOR_OCUPADA);  lblEstado.setText("🟡 OCUPADA");  lblEstado.setForeground(new Color(160,100,0)); }
                case PAUSADA     -> { setBackground(COLOR_PAUSADA);  lblEstado.setText("⏸ PAUSADA");  lblEstado.setForeground(Color.DARK_GRAY); }
                case DESACTIVADA -> { setBackground(COLOR_INACTIVA); lblEstado.setText("🔴 INACTIVA"); lblEstado.setForeground(Color.RED); }
                default          -> setBackground(COLOR_LIBRE);
            }

            if (caja.getSocioActual() != null) {
                var socio = caja.getSocioActual();
                lblFicha.setText(socio.getFicha());
                String desc = socio.getDescripcionServicios();
                lblServicios.setText(desc.length() > 28 ? desc.substring(0, 25) + "..." : desc);
                lblServicios.setToolTipText(desc);
                lblMonto.setText(String.format("Bs %.2f", socio.getMonto()));
                long mins = caja.getMinutosEnAtencionActual(tiempoSimulado);
                lblTimer.setText(mins + " / " + socio.getDuracionEstimada() + " min");
                lblTimer.setForeground(mins > socio.getDuracionEstimada() ? Color.RED : new Color(0, 80, 160));
            } else {
                lblFicha.setText("-");
                lblServicios.setText("-");
                lblMonto.setText("-");
                lblTimer.setText("—");
                lblTimer.setForeground(Color.DARK_GRAY);
            }

            lblAtendidos.setText("Atendidos: " + caja.getTotalAtendidos()
                    + "  |  Bs " + String.format("%.0f", caja.getMontoTotalAtendido()));
            areaResumen.setText(caja.getResumenTiempoServicios());
        }

        void limpiar() {
            lblFicha.setText("-"); lblServicios.setText("-");
            lblMonto.setText("-"); lblTimer.setText("—");
            lblAtendidos.setText("Atendidos: 0");
            areaResumen.setText("");
        }

        private static JLabel bold(String text, int size) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("SansSerif", Font.BOLD, size));
            return l;
        }
    }
}