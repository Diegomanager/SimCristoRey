package com.supermercado.presentation.cooperativa.panel;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.EstadoCaja;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel de ventanillas agrupadas por tipo en secciones con borde, sin pestañas.
 * Historial de cada caja más grande (5 filas).
 */
public class PanelVentanillas extends JPanel {

    private List<Caja> cajas       = new ArrayList<>();
    private long       tiempoActual= 0;
    private final Map<String, TarjetaCaja> tarjetas = new LinkedHashMap<>();

    // Grupos de cajas por prefijo de ID
    private static final String[] GRUPOS    = { "G-", "P-", "MM-" };
    private static final String[] ETIQUETAS = {
        "🏦  Cajas Generales",
        "🖥  Cajas de Plataforma",
        "💰  Montos Mayores (MM)"
    };
    private static final Color[] COLORES = {
        new Color(210, 240, 210),
        new Color(210, 225, 255),
        new Color(255, 245, 200)
    };

    private final JPanel panelPrincipal = new JPanel();
    private final JScrollPane scroll;

    public PanelVentanillas() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🖥  Ventanillas",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 13)));

        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        scroll = new JScrollPane(panelPrincipal);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void setCajas(List<Caja> cajas) {
        this.cajas = cajas != null ? cajas : new ArrayList<>();
        SwingUtilities.invokeLater(this::construirGrupos);
    }

    private void construirGrupos() {
        panelPrincipal.removeAll();
        tarjetas.clear();

        for (int g = 0; g < GRUPOS.length; g++) {
            final String prefijo   = GRUPOS[g];
            final String etiqueta  = ETIQUETAS[g];
            final Color  color     = COLORES[g];

            List<Caja> grupo = new ArrayList<>();
            for (Caja c : cajas) {
                if (c.getId() != null && c.getId().startsWith(prefijo)) grupo.add(c);
            }
            if (grupo.isEmpty()) continue;

            // Panel del grupo con borde
            JPanel panelGrupo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelGrupo.setBackground(color);
            panelGrupo.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(color.darker(), 1, true),
                    etiqueta + " (" + grupo.size() + " cajas)",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("SansSerif", Font.BOLD, 12)));

            for (Caja caja : grupo) {
                TarjetaCaja t = new TarjetaCaja(caja);
                tarjetas.put(caja.getId(), t);
                panelGrupo.add(t);
            }

            panelPrincipal.add(panelGrupo);
            panelPrincipal.add(Box.createVerticalStrut(8));
        }

        // Otras cajas (sin prefijo conocido)
        List<Caja> otras = new ArrayList<>();
        for (Caja c : cajas) {
            boolean encontrado = false;
            for (String p : GRUPOS) if (c.getId() != null && c.getId().startsWith(p)) { encontrado=true; break; }
            if (!encontrado) otras.add(c);
        }
        if (!otras.isEmpty()) {
            JPanel panelOtras = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelOtras.setBackground(new Color(240,240,240));
            panelOtras.setBorder(BorderFactory.createTitledBorder(
                    "📋 Otras cajas (" + otras.size() + ")"));
            for (Caja c : otras) {
                TarjetaCaja t = new TarjetaCaja(c);
                tarjetas.put(c.getId(), t);
                panelOtras.add(t);
            }
            panelPrincipal.add(panelOtras);
        }

        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }

    public void actualizar(List<Caja> cajas, long tiempoSim) {
        this.cajas = cajas; this.tiempoActual = tiempoSim;
        SwingUtilities.invokeLater(() -> {
            for (Caja c : cajas) {
                TarjetaCaja t = tarjetas.get(c.getId());
                if (t != null) t.actualizar(c, tiempoSim);
            }
        });
    }

    public void actualizar(List<Caja> cajas) { actualizar(cajas, tiempoActual); }
    public void limpiar() { tarjetas.values().forEach(TarjetaCaja::limpiar); }

    // ── Tarjeta individual ────────────────────────────────────────────────────
    static class TarjetaCaja extends JPanel {

        private static final Color C_LIBRE    = new Color(200, 240, 200);
        private static final Color C_OCUPADA  = new Color(255, 240, 180);
        private static final Color C_PAUSADA  = new Color(220, 220, 220);
        private static final Color C_INACTIVA = new Color(200, 200, 200);

        private final JLabel lblId       = bold("---", 14);
        private final JLabel lblEstado   = bold("LIBRE", 11);
        private final JLabel lblFicha    = new JLabel("-");
        private final JLabel lblServicio = new JLabel("-");
        private final JLabel lblTimer    = bold("—", 12);
        private final JLabel lblAtend    = new JLabel("Atend: 0");
        private final JTextArea areaRes  = new JTextArea(5, 20); // más grande

        TarjetaCaja(Caja caja) {
            setLayout(new BorderLayout(3, 3));
            setPreferredSize(new Dimension(220, 220));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(160, 160, 160), 1, true),
                    BorderFactory.createEmptyBorder(5, 7, 5, 7)));

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.add(lblId, BorderLayout.WEST);
            header.add(lblEstado, BorderLayout.EAST);

            JPanel body = new JPanel(new GridLayout(0, 1, 1, 1));
            body.setOpaque(false);
            body.add(lbl("Ticket:")); body.add(lblFicha);
            body.add(lbl("Servicio:")); body.add(lblServicio);
            body.add(lbl("⏱ En atención:")); body.add(lblTimer);
            body.add(lblAtend);

            areaRes.setEditable(false);
            areaRes.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            areaRes.setBackground(new Color(245, 248, 255));
            JScrollPane sc = new JScrollPane(areaRes);
            sc.getVerticalScrollBar().setUnitIncrement(16);
            sc.setBorder(BorderFactory.createTitledBorder("Historial (últimos)"));
            sc.setPreferredSize(new Dimension(200, 80));

            add(header, BorderLayout.NORTH);
            add(body,   BorderLayout.CENTER);
            add(sc,     BorderLayout.SOUTH);

            actualizar(caja, 0);
        }

        void actualizar(Caja caja, long tiempoSim) {
            lblId.setText(caja.getId());
            switch (caja.getEstado()) {
                case LIBRE       -> { setBackground(C_LIBRE);    lblEstado.setText("🟢 LIBRE");    lblEstado.setForeground(new Color(0,130,0)); }
                case OCUPADA     -> { setBackground(C_OCUPADA);  lblEstado.setText("🟡 OCUPADA");  lblEstado.setForeground(new Color(160,100,0)); }
                case PAUSADA     -> { setBackground(C_PAUSADA);  lblEstado.setText("⏸ PAUSADA");  lblEstado.setForeground(Color.DARK_GRAY); }
                case DESACTIVADA -> { setBackground(C_INACTIVA); lblEstado.setText("🔴 INACT.");   lblEstado.setForeground(Color.RED); }
                default          -> setBackground(C_LIBRE);
            }
            if (caja.getSocioActual() != null) {
                var s = caja.getSocioActual();
                lblFicha.setText(s.getFicha());
                String desc = s.getDescripcionServicios();
                lblServicio.setText(desc.length()>26 ? desc.substring(0,23)+"..." : desc);
                lblServicio.setToolTipText(desc);
                long mins = caja.getMinutosEnAtencionActual(tiempoSim);
                lblTimer.setText(mins + " / " + s.getDuracionEstimada() + " min");
                lblTimer.setForeground(mins > s.getDuracionEstimada()
                        ? Color.RED : new Color(0,80,160));
            } else {
                lblFicha.setText("—"); lblServicio.setText("—");
                lblTimer.setText("—"); lblTimer.setForeground(Color.DARK_GRAY);
            }
            lblAtend.setText("Atend: " + caja.getTotalAtendidos()
                    + "  |  Bs " + String.format("%.0f", caja.getMontoTotalAtendido()));
            areaRes.setText(caja.getResumenTiempoServicios());
        }

        void limpiar() {
            lblFicha.setText("—"); lblServicio.setText("—");
            lblTimer.setText("—"); lblAtend.setText("Atend: 0"); areaRes.setText("");
        }

        private static JLabel bold(String t, int sz) {
            JLabel l = new JLabel(t); l.setFont(new Font("SansSerif",Font.BOLD,sz)); return l;
        }
        private static JLabel lbl(String t) { return new JLabel(t); }
    }
}