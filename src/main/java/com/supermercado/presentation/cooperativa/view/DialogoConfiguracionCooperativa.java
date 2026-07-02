package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.service.SimuladorCooperativaService;

import javax.swing.*;
import java.awt.*;

public class DialogoConfiguracionCooperativa extends JDialog {

    private final JSpinner spnDuracionSimulada = new JSpinner(new SpinnerNumberModel(480, 60, 1440, 30));
    private final JSpinner spnDuracionReal     = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
    private final JSpinner spnMaxSocios        = new JSpinner(new SpinnerNumberModel(400, 10, 2000, 10));
    private final JSpinner spnIntervalo        = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));
    private final JLabel   lblEscalaCalc       = new JLabel("≈ 62 ms/min");

    private boolean confirmado = false;
    private int    duracionSimuladaMin;
    private long   msPorMinuto;
    private int    maxSocios;
    private double intervaloLlegada;

    public DialogoConfiguracionCooperativa(Frame owner) {
        super(owner, "Configuracion de Simulacion", true);
        initUI();
        actualizarEscala();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addFila(panel, c, row++, "Duracion simulada (min):", spnDuracionSimulada,
                "Duracion de la jornada laboral en minutos simulados (ej. 480 = 8 horas)");
        addFila(panel, c, row++, "Duracion real deseada (seg):", spnDuracionReal,
                "En cuantos segundos reales debe completarse la simulacion");
        addFila(panel, c, row++, "Escala calculada:", lblEscalaCalc,
                "Milisegundos por minuto simulado (se calcula automaticamente)");
        addFila(panel, c, row++, "Max. socios:", spnMaxSocios,
                "Limite maximo de socios a generar");
        addFila(panel, c, row++, "Intervalo llegada (min sim):", spnIntervalo,
                "Cada cuantos minutos simulados llega un nuevo socio");

        spnDuracionSimulada.addChangeListener(e -> actualizarEscala());
        spnDuracionReal.addChangeListener(e -> actualizarEscala());

        JButton btnOk     = new JButton("Aceptar");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> { guardar(); dispose(); });
        btnCancel.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancel);
        botones.add(btnOk);

        setLayout(new BorderLayout());
        add(panel,   BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void addFila(JPanel p, GridBagConstraints c, int row,
                         String label, JComponent comp, String tooltip) {
        c.gridx = 0; c.gridy = row; c.weightx = 0.3;
        p.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 0.7;
        comp.setToolTipText(tooltip);
        p.add(comp, c);
    }

    private void actualizarEscala() {
        int simMin  = (int) spnDuracionSimulada.getValue();
        int realSeg = (int) spnDuracionReal.getValue();
        long ms = SimuladorCooperativaService.calcularEscala(simMin, realSeg);
        lblEscalaCalc.setText(ms + " ms/min simulado");
        lblEscalaCalc.setForeground(ms < 50 ? Color.RED : new Color(0, 100, 0));
    }

    private void guardar() {
        duracionSimuladaMin = (int)    spnDuracionSimulada.getValue();
        int realSeg         = (int)    spnDuracionReal.getValue();
        msPorMinuto         = SimuladorCooperativaService.calcularEscala(duracionSimuladaMin, realSeg);
        maxSocios           = (int)    spnMaxSocios.getValue();
        intervaloLlegada    = (double) spnIntervalo.getValue();
        confirmado          = true;
    }

    public boolean isConfirmado()        { return confirmado; }
    public int     getDuracionSimulada() { return duracionSimuladaMin; }
    public long    getMsPorMinuto()      { return msPorMinuto; }
    public int     getMaxSocios()        { return maxSocios; }
    public double  getIntervaloLlegada() { return intervaloLlegada; }
}