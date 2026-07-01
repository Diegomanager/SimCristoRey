package com.supermercado.presentation.supermercado.view;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DialogoConfiguracion extends JDialog {

    private static final long serialVersionUID = 1L;

    private JSpinner spinnerCajasNormales;
    private JSpinner spinnerCajasRapidas;
    private JSpinner spinnerHoras;
    private JSpinner spinnerDuracion;
    private JSpinner spinnerLimiteClientes;
    private JSpinner spinnerProbabilidad;
    private JSpinner spinnerArticulosMin;
    private JSpinner spinnerArticulosMax;
    private JSpinner spinnerTiempoNormalMin;
    private JSpinner spinnerTiempoNormalMax;
    private JSpinner spinnerTiempoRapidaMin;
    private JSpinner spinnerTiempoRapidaMax;
    private JSpinner spinnerLimiteRapido;

    private boolean confirmado = false;
    private ConfiguracionDTO configuracionOriginal;

    private static final Font FONT_SECCION = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_TITULO  = new Font("Segoe UI", Font.BOLD,  16);
    private static final Color COLOR_SECCION = new Color(41, 98, 155);
    private static final Color COLOR_LABEL   = new Color(50, 50, 50);
    private static final Color COLOR_BG      = new Color(245, 245, 245);
    private static final Color COLOR_PANEL   = Color.WHITE;

    public DialogoConfiguracion(JFrame parent, ConfiguracionDTO config) {
        super(parent, "Configuracion de la Simulacion", true);
        this.configuracionOriginal = config;
        setSize(580, 720);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
        pp.setBackground(COLOR_BG);
        pp.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JLabel lblTitulo = new JLabel("Configuracion del Simulador de Supermercado");
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(new Color(30, 30, 30));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pp.add(lblTitulo);
        pp.add(Box.createVerticalStrut(18));

        // ---- 1. Tiempo de Simulacion ----
        pp.add(crearSeccion("1. Tiempo de Simulacion"));
        pp.add(Box.createVerticalStrut(5));
        JPanel s1 = crearPanelCampos();
        agregarCampo(s1, "Horas simuladas (duracion del dia):",
            spinnerHoras = crearSpinner(configuracionOriginal.getHorasSimuladas(), 1, 24), 0);
        agregarCampo(s1, "Duracion real (segundos):",
            spinnerDuracion = crearSpinner(configuracionOriginal.getDuracionRealSegundos(), 5, 120), 1);
        pp.add(s1);
        pp.add(Box.createVerticalStrut(12));

        // ---- 2. Cajas Registradoras ----
        pp.add(crearSeccion("2. Cajas Registradoras"));
        pp.add(Box.createVerticalStrut(5));
        JPanel s2 = crearPanelCampos();
        agregarCampo(s2, "Cajas Normales:",
            spinnerCajasNormales = crearSpinner(configuracionOriginal.getNumCajasNormales(), 0, 20), 0);
        agregarCampo(s2, "Cajas Rapidas:",
            spinnerCajasRapidas = crearSpinner(configuracionOriginal.getNumCajasRapidas(), 0, 10), 1);
        pp.add(s2);
        pp.add(Box.createVerticalStrut(12));

        // ---- 3. Flujo de Clientes ----
        pp.add(crearSeccion("3. Flujo de Clientes"));
        pp.add(Box.createVerticalStrut(5));
        JPanel s3 = crearPanelCampos();
        agregarCampo(s3, "Probabilidad llegada / tick (%):",
            spinnerProbabilidad = crearSpinner(configuracionOriginal.getProbabilidadLlegadaCliente(), 1, 100), 0);
        agregarCampo(s3, "Max. clientes (0 = sin limite):",
            spinnerLimiteClientes = crearSpinner(configuracionOriginal.getLimiteClientes(), 0, 10000), 1);
        pp.add(s3);
        pp.add(Box.createVerticalStrut(12));

        // ---- 4. Tiempos de Atencion ----
        pp.add(crearSeccion("4. Tiempos de Atencion (minutos simulados)"));
        pp.add(Box.createVerticalStrut(5));
        JPanel s4 = crearPanelCampos();
        agregarCampoDual(s4,
            "Normal minimo:",
            spinnerTiempoNormalMin = crearSpinner(configuracionOriginal.getTiempoCajaNormalMin(), 1, 30),
            "Normal maximo:",
            spinnerTiempoNormalMax = crearSpinner(configuracionOriginal.getTiempoCajaNormalMax(), 1, 30), 0);
        agregarCampoDual(s4,
            "Rapida minimo:",
            spinnerTiempoRapidaMin = crearSpinner(configuracionOriginal.getTiempoCajaRapidaMin(), 1, 15),
            "Rapida maximo:",
            spinnerTiempoRapidaMax = crearSpinner(configuracionOriginal.getTiempoCajaRapidaMax(), 1, 15), 1);
        pp.add(s4);
        pp.add(Box.createVerticalStrut(12));

        // ---- 5. Articulos por Cliente ----
        pp.add(crearSeccion("5. Articulos por Cliente"));
        pp.add(Box.createVerticalStrut(5));
        JPanel s5 = crearPanelCampos();
        agregarCampoDual(s5,
            "Minimo:",
            spinnerArticulosMin = crearSpinner(configuracionOriginal.getArticulosClienteMin(), 1, 10000),
            "Maximo:",
            spinnerArticulosMax = crearSpinner(configuracionOriginal.getArticulosClienteMax(), 1, 10000), 0);
        // Campo para límite de cliente rápido
        agregarCampo(s5, "Límite artículos para caja rápida:",
            spinnerLimiteRapido = crearSpinner(configuracionOriginal.getLimiteClienteRapido(), 1, 100), 1);
        pp.add(s5);
        pp.add(Box.createVerticalStrut(10));

        JScrollPane scroll = new JScrollPane(pp);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setBlockIncrement(60);
        add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        panelBotones.setBackground(COLOR_BG);
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)));

        JButton btnCancelar = crearBotonDialogo("Cancelar",         new Color(160, 160, 160));
        JButton btnIniciar  = crearBotonDialogo("Iniciar Simulacion", new Color(41,  98, 155));
        btnCancelar.addActionListener(e -> { confirmado = false; dispose(); });
        btnIniciar.addActionListener(e -> guardar());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnIniciar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearSeccion(String texto) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(COLOR_BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SECCION); lbl.setForeground(COLOR_SECCION);
        p.add(lbl, BorderLayout.WEST);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 210, 225));
        p.add(sep, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelCampos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COLOR_PANEL);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return p;
    }

    private void agregarCampo(JPanel panel, String labelTxt, JSpinner spinner, int fila) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.gridy = fila;
        gbc.gridx = 0; gbc.weightx = 0.65;
        JLabel lbl = new JLabel(labelTxt);
        lbl.setFont(FONT_LABEL); lbl.setForeground(COLOR_LABEL);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.35;
        panel.add(spinner, gbc);
    }

    private void agregarCampoDual(JPanel panel,
            String lbl1, JSpinner sp1, String lbl2, JSpinner sp2, int fila) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.gridy = fila;
        gbc.gridx = 0; gbc.weightx = 0.28;
        JLabel l1 = new JLabel(lbl1); l1.setFont(FONT_LABEL); l1.setForeground(COLOR_LABEL);
        panel.add(l1, gbc);
        gbc.gridx = 1; gbc.weightx = 0.22; panel.add(sp1, gbc);
        gbc.gridx = 2; gbc.weightx = 0.28;
        JLabel l2 = new JLabel(lbl2); l2.setFont(FONT_LABEL); l2.setForeground(COLOR_LABEL);
        panel.add(l2, gbc);
        gbc.gridx = 3; gbc.weightx = 0.22; panel.add(sp2, gbc);
    }

    private JSpinner crearSpinner(int valor, int min, int max) {
        int v = Math.max(min, Math.min(max, valor));
        JSpinner sp = new JSpinner(new SpinnerNumberModel(v, min, max, 1));
        sp.setPreferredSize(new Dimension(90, 26));
        sp.setFont(FONT_LABEL);
        return sp;
    }

    private JButton crearBotonDialogo(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(fondo); btn.setForeground(Color.WHITE);
        btn.setOpaque(true); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fondo.darker(), 1),
            BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(fondo.brighter()); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(fondo); }
        });
        return btn;
    }

    private void guardar() {
        try {
            ConfiguracionDTO config = new ConfiguracionDTO.Builder()
                .numCajasNormales((int) spinnerCajasNormales.getValue())
                .numCajasRapidas((int) spinnerCajasRapidas.getValue())
                .horasSimuladas((int) spinnerHoras.getValue())
                .duracionRealSegundos((int) spinnerDuracion.getValue())
                .limiteClientes((int) spinnerLimiteClientes.getValue())
                .probabilidadLlegadaCliente((int) spinnerProbabilidad.getValue())
                .articulosClienteMin((int) spinnerArticulosMin.getValue())
                .articulosClienteMax((int) spinnerArticulosMax.getValue())
                .tiempoCajaNormalMin((int) spinnerTiempoNormalMin.getValue())
                .tiempoCajaNormalMax((int) spinnerTiempoNormalMax.getValue())
                .tiempoCajaRapidaMin((int) spinnerTiempoRapidaMin.getValue())
                .tiempoCajaRapidaMax((int) spinnerTiempoRapidaMax.getValue())
                .limiteClienteRapido((int) spinnerLimiteRapido.getValue())
                .build();
            configuracionOriginal = config;
            confirmado = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar la configuracion: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() { return confirmado; }
    public ConfiguracionDTO getConfiguracion() { return configuracionOriginal; }
}