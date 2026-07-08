package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.service.EstadisticasFase;
import com.supermercado.domain.cooperativa.service.EstadisticasFinancierasService;
import com.supermercado.domain.cooperativa.service.SimuladorCooperativaService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;

/**
 * Panel de estadisticas con 3 secciones compactas y proporcionales.
 * Fila superior: Dia actual (izq) | Acumulado global (der)
 * Dentro de cada columna: Principal | Rezagados | Total
 * Fila inferior (ocultable): resumen final de texto
 */
public class PanelEstadisticasFinancieras extends JPanel {

    // Fuentes compactas
    private static final Font F_LBL = new Font("SansSerif", Font.PLAIN, 10);
    private static final Font F_VAL = new Font("SansSerif", Font.BOLD,  10);
    private static final Font F_HDR = new Font("SansSerif", Font.BOLD,  11);
    private static final Color C_VAL = new Color(0, 70, 150);

    // ── DIA ACTUAL ────────────────────────────────────────────────────────────
    // Principal dia
    private final JLabel dP_atend = v(); private final JLabel dP_monto = v();
    private final JLabel dP_esp   = v(); private final JLabel dP_aten  = v();
    private final JLabel dP_cajero= v();
    // Rezagados dia
    private final JLabel dR_atend = v(); private final JLabel dR_monto = v();
    private final JLabel dR_esp   = v(); private final JLabel dR_aten  = v();
    private final JLabel dR_cajero= v();
    // Total dia
    private final JLabel dT_gen   = v(); private final JLabel dT_atend = v();
    private final JLabel dT_noat  = v(); private final JLabel dT_monto = v();
    private final JLabel dT_esp   = v(); private final JLabel dT_aten  = v();

    // ── ACUMULADO GLOBAL ──────────────────────────────────────────────────────
    // Principal acum
    private final JLabel aP_atend = v(); private final JLabel aP_monto = v();
    private final JLabel aP_esp   = v(); private final JLabel aP_aten  = v();
    private final JLabel aP_cajero= v();
    // Rezagados acum
    private final JLabel aR_atend = v(); private final JLabel aR_monto = v();
    private final JLabel aR_esp   = v(); private final JLabel aR_aten  = v();
    // Total acum
    private final JLabel aT_dias  = v(); private final JLabel aT_gen   = v();
    private final JLabel aT_atend = v(); private final JLabel aT_noat  = v();
    private final JLabel aT_monto = v(); private final JLabel aT_esp   = v();
    private final JLabel aT_aten  = v(); private final JLabel aT_cajero= v();

    private final JTextArea areaResumen;
    private final JScrollPane scResumen;
    private final NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es","BO"));

    public PanelEstadisticasFinancieras() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Estadisticas Financieras",
                TitledBorder.LEFT, TitledBorder.TOP, F_HDR));
        nf.setMaximumFractionDigits(2); nf.setMinimumFractionDigits(2);

        // Panel central: dia | acumulado
        JPanel centro = new JPanel(new GridLayout(1, 2, 6, 0));
        centro.add(columna("Dia Actual",       bloquesPpal(dP_atend,dP_monto,dP_esp,dP_aten,dP_cajero),
                                                bloquesRez(dR_atend,dR_monto,dR_esp,dR_aten,dR_cajero),
                                                bloquesTotalDia()));
        centro.add(columna("Acumulado Global", bloquesPpal(aP_atend,aP_monto,aP_esp,aP_aten,aP_cajero),
                                                bloquesRez(aR_atend,aR_monto,aR_esp,aR_aten,null),
                                                bloquesTotalAcum()));

        // Resumen final (texto, oculto hasta el final)
        areaResumen = new JTextArea();
        areaResumen.setEditable(false);
        areaResumen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        areaResumen.setBackground(new Color(245, 252, 245));
        areaResumen.setVisible(false);
        scResumen = new JScrollPane(areaResumen);
        scResumen.getVerticalScrollBar().setUnitIncrement(20);
        scResumen.setBorder(BorderFactory.createTitledBorder("Resumen Final"));
        scResumen.setVisible(false);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centro, scResumen);
        split.setResizeWeight(0.72);
        split.setDividerSize(5);
        add(split, BorderLayout.CENTER);
    }

    // ── Constructores de bloques ──────────────────────────────────────────────

    private JPanel bloquesPpal(JLabel atend, JLabel monto, JLabel esp,
                                JLabel aten, JLabel cajero) {
        JPanel p = seccion("Principal");
        fila(p, "Atendidos:", atend);
        fila(p, "Monto (Bs):", monto);
        fila(p, "Espera (min):", esp);
        fila(p, "Atencion (min):", aten);
        fila(p, "Cajero:", cajero);
        return p;
    }

    private JPanel bloquesRez(JLabel atend, JLabel monto, JLabel esp,
                               JLabel aten, JLabel cajero) {
        JPanel p = seccion("Rezagados");
        fila(p, "Atendidos:", atend);
        fila(p, "Monto (Bs):", monto);
        fila(p, "Espera (min):", esp);
        fila(p, "Atencion (min):", aten);
        if (cajero != null) fila(p, "Cajero:", cajero);
        return p;
    }

    private JPanel bloquesTotalDia() {
        JPanel p = seccion("Total dia");
        fila(p, "Generados:", dT_gen);
        fila(p, "Atendidos:", dT_atend);
        fila(p, "No atend.:", dT_noat);
        fila(p, "Monto (Bs):", dT_monto);
        fila(p, "Espera (min):", dT_esp);
        fila(p, "Atencion (min):", dT_aten);
        return p;
    }

    private JPanel bloquesTotalAcum() {
        JPanel p = seccion("Total");
        fila(p, "Dias:", aT_dias);
        fila(p, "Generados:", aT_gen);
        fila(p, "Atendidos:", aT_atend);
        fila(p, "No atend.:", aT_noat);
        fila(p, "Monto (Bs):", aT_monto);
        fila(p, "Espera (min):", aT_esp);
        fila(p, "Atencion (min):", aT_aten);
        fila(p, "Cajero:", aT_cajero);
        return p;
    }

    /**
     * Columna con 3 secciones apiladas verticalmente:
     * Principal (arriba) | Rezagados (medio) | Total (abajo).
     * Todas tienen la misma altura relativa.
     */
    private JPanel columna(String titulo, JPanel ppal, JPanel rez, JPanel tot) {
        JPanel col = new JPanel(new GridLayout(3, 1, 0, 4));
        col.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), titulo,
                TitledBorder.LEFT, TitledBorder.TOP, F_HDR));
        col.add(ppal);
        col.add(rez);
        col.add(tot);
        return col;
    }

    private JPanel seccion(String titulo) {
        JPanel p = new JPanel(new GridLayout(0, 2, 2, 1));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180,180,200)),
                titulo, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 9)));
        return p;
    }

    private void fila(JPanel p, String lbl, JLabel val) {
        JLabel l = new JLabel(lbl); l.setFont(F_LBL);
        p.add(l); p.add(val);
    }

    private static JLabel v() {
        JLabel l = new JLabel("0"); l.setFont(F_VAL); l.setForeground(C_VAL); return l;
    }

    // ── Actualizar en tiempo real ─────────────────────────────────────────────
    public void actualizar(EstadisticasFinancierasService est, int totalGen) {
        if (est == null) return;
        SwingUtilities.invokeLater(() -> {
            EstadisticasFase p = est.getPrincipal();
            EstadisticasFase r = est.getRezagados();

            // Dia principal
            dP_atend.setText(p.getTotalAtendidos()+"");
            dP_monto.setText(nf.format(p.getMontoTotal()));
            dP_esp.setText(fmt1(p.getPromedioEspera()));
            dP_aten.setText(fmt1(p.getPromedioAtencion()));
            dP_cajero.setText(p.getCajeroEstrella());

            // Dia rezagados
            dR_atend.setText(r.getTotalAtendidos()+"");
            dR_monto.setText(nf.format(r.getMontoTotal()));
            dR_esp.setText(fmt1(r.getPromedioEspera()));
            dR_aten.setText(fmt1(r.getPromedioAtencion()));
            dR_cajero.setText(r.getCajeroEstrella());

            // Dia total
            int tot = est.getTotalAtendidos();
            dT_gen.setText(totalGen+"");
            dT_atend.setText(tot+"");
            dT_noat.setText(Math.max(0, totalGen - tot)+"");
            dT_monto.setText(nf.format(est.getMontoTotal()));
            dT_esp.setText(fmt1(est.getPromedioEspera()));
            dT_aten.setText(fmt1(est.getPromedioAtencion()));

            // Acumulado principal
            aP_atend.setText(est.getAcumAtendPpal()+"");
            aP_monto.setText(nf.format(est.getAcumMonto()));
            aP_esp.setText(fmt1(est.getAcumPromedioEspera()));
            aP_aten.setText(fmt1(est.getAcumPromedioAtencion()));
            aP_cajero.setText(est.getCajeroEstrellaGlobal());

            // Acumulado rezagados
            aR_atend.setText(est.getAcumAtendRez()+"");
            aR_monto.setText(nf.format(est.getAcumMonto()));
            aR_esp.setText(fmt1(est.getAcumPromedioEspera()));
            aR_aten.setText(fmt1(est.getAcumPromedioAtencion()));

            // Acumulado total
            aT_dias.setText(est.getDiasAcumulados()+"");
            aT_gen.setText(est.getAcumGenerados()+"");
            aT_atend.setText(est.getAcumTotalAtendidos()+"");
            aT_noat.setText(est.getAcumNoAtendidos()+"");
            aT_monto.setText(nf.format(est.getAcumMonto()));
            aT_esp.setText(fmt1(est.getAcumPromedioEspera()));
            aT_aten.setText(fmt1(est.getAcumPromedioAtencion()));
            aT_cajero.setText(est.getCajeroEstrellaGlobal());
        });
    }

    public void acumularDia(ResumenDiario r) { /* gestionado por EstadisticasFinancierasService */ }

    public void mostrarResumenFinal(SimuladorCooperativaService sim, long minSim) {
        SwingUtilities.invokeLater(() -> {
            var est  = sim.getEstadisticas();
            var p    = est.getPrincipal();
            var r    = est.getRezagados();
            int gen  = est.getAcumGenerados() > 0
                    ? est.getAcumGenerados()
                    : sim.getGenerador().getTotalGenerados();
            int at   = est.getAcumTotalAtendidos() > 0
                    ? est.getAcumTotalAtendidos()
                    : est.getTotalAtendidos();

            StringBuilder sb = new StringBuilder();
            sb.append("===== RESUMEN FINAL =====\n\n");
            sb.append("FASE PRINCIPAL\n");
            sb.append("  Atendidos : ").append(p.getTotalAtendidos()).append("\n");
            sb.append("  Monto     : Bs ").append(nf.format(p.getMontoTotal())).append("\n");
            sb.append("  Espera    : ").append(fmt1(p.getPromedioEspera())).append(" min\n");
            sb.append("  Atencion  : ").append(fmt1(p.getPromedioAtencion())).append(" min\n");
            sb.append("  Cajero    : ").append(p.getCajeroEstrella()).append("\n");
            if (minSim > 0)
                sb.append("  Eficiencia: ").append(String.format("%.3f", (double)p.getTotalAtendidos()/minSim)).append(" s/min\n");
            sb.append("\n");

            sb.append("FASE REZAGADOS\n");
            if (r.getTotalAtendidos() == 0) {
                sb.append("  Sin rezagados\n");
            } else {
                sb.append("  Atendidos : ").append(r.getTotalAtendidos()).append("\n");
                sb.append("  Monto     : Bs ").append(nf.format(r.getMontoTotal())).append("\n");
                sb.append("  Espera    : ").append(fmt1(r.getPromedioEspera())).append(" min\n");
                sb.append("  Atencion  : ").append(fmt1(r.getPromedioAtencion())).append(" min\n");
            }
            sb.append("\n");

            sb.append("TOTAL GLOBAL\n");
            sb.append("  Dias      : ").append(est.getDiasAcumulados()).append("\n");
            sb.append("  Generados : ").append(gen).append("\n");
            sb.append("  Atendidos : ").append(at).append("\n");
            sb.append("  No aten.  : ").append(est.getAcumNoAtendidos()).append("\n");
            sb.append("  Monto     : Bs ").append(nf.format(est.getAcumMonto())).append("\n");
            sb.append("  Espera    : ").append(fmt1(est.getAcumPromedioEspera())).append(" min\n");
            sb.append("  Atencion  : ").append(fmt1(est.getAcumPromedioAtencion())).append(" min\n");
            if (gen > 0)
                sb.append("  Tasa      : ").append(String.format("%.1f", (double)at/gen*100)).append("%\n");
            if (minSim > 0)
                sb.append("  Eficiencia: ").append(String.format("%.3f", est.getEficienciaGlobal(minSim))).append(" s/min\n");

            areaResumen.setText(sb.toString());
            areaResumen.setCaretPosition(0);
            areaResumen.setVisible(true);
            scResumen.setVisible(true);
            revalidate(); repaint();
        });
    }

    public void limpiarResumen() {
        SwingUtilities.invokeLater(() -> {
            areaResumen.setText("");
            areaResumen.setVisible(false);
            scResumen.setVisible(false);
        });
    }

    private static String fmt1(double v) { return String.format("%.1f", v); }
}