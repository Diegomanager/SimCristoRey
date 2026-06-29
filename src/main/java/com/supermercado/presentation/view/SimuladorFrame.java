package com.supermercado.presentation.view;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.dto.EstadisticasDTO;
import com.supermercado.application.port.IConfiguracionRepositorio;
import com.supermercado.application.port.ILogService;
import com.supermercado.presentation.controller.SimulacionController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SimuladorFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ILogService logService;
    private final IConfiguracionRepositorio configRepo;
    private SimulacionController controller;
    private ConfiguracionDTO configuracion;

    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnPausar;
    private JButton btnReiniciar;
    private JTextArea areaLog;
    private PanelCajas panelCajas;
    private PanelEstadisticas panelStats;
    private JButton btnConfigurar;

    public SimuladorFrame(ILogService logService, IConfiguracionRepositorio configRepo) {
        this.logService = logService;
        this.configRepo = configRepo;
        this.configuracion = configRepo.cargar();

        setTitle("Simulador de Supermercado - Gestion de Calidad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(Theme.BACKGROUND);

        inicializarComponentes();
    }

    public void setController(SimulacionController controller) {
        this.controller = controller;
    }

    public void setConfiguracion(ConfiguracionDTO configuracion) {
        this.configuracion = configuracion;
        if (panelCajas != null && configuracion != null) {
            int total = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
            panelCajas.inicializarCajas(total);
            for (int i = 1; i <= configuracion.getNumCajasRapidas(); i++)
                panelCajas.actualizarCaja(i, "LIBRE", 0, "", "R");
            for (int i = configuracion.getNumCajasRapidas() + 1; i <= total; i++)
                panelCajas.actualizarCaja(i, "LIBRE", 0, "", "N");
        }
    }

    private void inicializarComponentes() {
        add(crearPanelControl(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(4);
        splitPane.setBackground(Theme.BACKGROUND);

        panelCajas = new PanelCajas();

        JScrollPane scrollCajas = new JScrollPane(panelCajas);
        scrollCajas.getVerticalScrollBar().setUnitIncrement(20);
        scrollCajas.getHorizontalScrollBar().setUnitIncrement(20);
        scrollCajas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHT, 1),
            "Estado de Cajas",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            Theme.PRIMARY_BLUE
        ));
        scrollCajas.getViewport().setBackground(Theme.BACKGROUND);

        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.setBackground(Theme.BACKGROUND);

        areaLog = new JTextArea(20, 30);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setBackground(Color.WHITE);
        areaLog.setForeground(new Color(30, 30, 30));
        areaLog.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 6));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.getVerticalScrollBar().setUnitIncrement(20);
        scrollLog.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHT, 1),
            "Log de Eventos",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            Theme.PRIMARY_BLUE
        ));

        panelStats = new PanelEstadisticas();
        JScrollPane scrollStats = new JScrollPane(panelStats);
        scrollStats.getVerticalScrollBar().setUnitIncrement(20);
        scrollStats.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Theme.BORDER_LIGHT, 1),
            "Estadisticas en Tiempo Real",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            Theme.PRIMARY_BLUE
        ));
        scrollStats.getViewport().setBackground(Color.WHITE);

        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitDerecho.setResizeWeight(0.55);
        splitDerecho.setDividerSize(4);
        splitDerecho.setTopComponent(scrollLog);
        splitDerecho.setBottomComponent(scrollStats);
        panelDerecho.add(splitDerecho, BorderLayout.CENTER);

        splitPane.setLeftComponent(scrollCajas);
        splitPane.setRightComponent(panelDerecho);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelControl() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Theme.PRIMARY_BLUE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.PRIMARY_DARK),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel("Simulador de Supermercado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblVersion = new JLabel("v2.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(200, 220, 245));
        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblVersion);

        Dimension dimBoton = new Dimension(100, 32);
        btnIniciar    = crearBoton("Iniciar",    new Color(55, 135, 55));
        btnPausar     = crearBoton("Pausar",     new Color(80, 100, 140));
        btnDetener    = crearBoton("Detener",    new Color(160, 50,  40));
        btnReiniciar  = crearBoton("Reiniciar",  new Color(80, 100, 140));
        btnConfigurar = crearBoton("Configurar", new Color(80, 100, 140));

        btnIniciar.setPreferredSize(dimBoton);
        btnPausar.setPreferredSize(dimBoton);
        btnDetener.setPreferredSize(dimBoton);
        btnReiniciar.setPreferredSize(dimBoton);
        btnConfigurar.setPreferredSize(new Dimension(105, 32));

        btnPausar.setEnabled(false);
        btnDetener.setEnabled(false);

        btnIniciar.addActionListener(e -> {
            if (controller != null && configuracion != null) {
                int total = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
                panelCajas.inicializarCajas(total);
                for (int i = 1; i <= configuracion.getNumCajasRapidas(); i++)
                    panelCajas.actualizarCaja(i, "LIBRE", 0, "", "R");
                for (int i = configuracion.getNumCajasRapidas() + 1; i <= total; i++)
                    panelCajas.actualizarCaja(i, "LIBRE", 0, "", "N");
                controller.iniciarSimulacion(configuracion);
            }
        });

        btnPausar.addActionListener(e -> {
            if (controller != null && controller.isSimulacionActiva())
                controller.pausarSimulacion();
        });

        btnDetener.addActionListener(e -> {
            if (controller != null) {
                controller.detenerSimulacion();
                btnPausar.setText("Pausar");
                btnPausar.setEnabled(false);
                cambiarEstadoPausa(false);
            }
        });

        btnReiniciar.addActionListener(e -> {
            if (controller != null) {
                controller.detenerSimulacion();
                reiniciarVista();
                agregarLog("Sistema reiniciado");
                logService.info("Sistema reiniciado");
            }
        });

        btnConfigurar.addActionListener(e -> {
            DialogoConfiguracion dialogo = new DialogoConfiguracion(SimuladorFrame.this, this.configuracion);
            dialogo.setVisible(true);
            if (dialogo.isConfirmado()) {
                this.configuracion = dialogo.getConfiguracion();
                configRepo.guardar(this.configuracion);
                int total = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
                panelCajas.inicializarCajas(total);
                for (int i = 1; i <= configuracion.getNumCajasRapidas(); i++)
                    panelCajas.actualizarCaja(i, "LIBRE", 0, "", "R");
                for (int i = configuracion.getNumCajasRapidas() + 1; i <= total; i++)
                    panelCajas.actualizarCaja(i, "LIBRE", 0, "", "N");
                agregarLog("Configuracion actualizada: " + total + " cajas");
                logService.info("Configuracion actualizada desde GUI");
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnIniciar);
        panelBotones.add(btnPausar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnReiniciar);
        panelBotones.add(btnConfigurar);

        panel.add(panelTitulo,  BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(fondo);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fondo.darker(), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(fondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(fondo);
            }
        });
        return btn;
    }

    private void reiniciarVista() {
        areaLog.setText("");
        panelCajas.reiniciar();
        panelStats.reiniciar();
        btnIniciar.setEnabled(true);
        btnDetener.setEnabled(false);
        btnPausar.setEnabled(false);
        btnPausar.setText("Pausar");
        btnPausar.setBackground(new Color(80, 100, 140));
        cambiarEstadoPausa(false);
        if (configuracion != null) {
            int total = configuracion.getNumCajasNormales() + configuracion.getNumCajasRapidas();
            panelCajas.inicializarCajas(total);
            for (int i = 1; i <= configuracion.getNumCajasRapidas(); i++)
                panelCajas.actualizarCaja(i, "LIBRE", 0, "", "R");
            for (int i = configuracion.getNumCajasRapidas() + 1; i <= total; i++)
                panelCajas.actualizarCaja(i, "LIBRE", 0, "", "N");
        }
    }

    public void limpiarLog() { areaLog.setText(""); }

    public void habilitarBotonIniciar(boolean habilitado) {
        btnIniciar.setEnabled(habilitado);
    }

    public void habilitarBotonDetener(boolean habilitado) {
        btnDetener.setEnabled(habilitado);
        if (habilitado) {
            btnPausar.setEnabled(true);
            btnPausar.setText("Pausar");
            cambiarEstadoPausa(false);
        } else {
            btnPausar.setEnabled(false);
        }
    }

    public void cambiarEstadoPausa(boolean pausado) {
        SwingUtilities.invokeLater(() -> {
            btnPausar.setText(pausado ? "Reanudar" : "Pausar");
            btnPausar.setBackground(pausado ? new Color(55, 135, 55) : new Color(80, 100, 140));
        });
    }

    public void actualizarEstadisticasDirecto(EstadisticasDTO estadisticas) {
        if (panelStats != null && estadisticas != null) {
            String hora = estadisticas.getHoraSimulada();
            String horaFinal = (hora == null || hora.isEmpty()) ? "08:00" : hora;
            SwingUtilities.invokeLater(() -> {
                panelStats.actualizarEstadisticasAvanzadas(
                    horaFinal,
                    estadisticas.getTotalClientesAtendidos(),
                    estadisticas.getTotalArticulosVendidos(),
                    estadisticas.getClientesEnCola(),
                    estadisticas.getClientesGenerados(),
                    estadisticas.getTiempoPromedioAtencion(),
                    estadisticas.getCajeroEstrella()
                );
                revalidate();
                repaint();
            });
        }
    }

    public void actualizarEstadisticas(EstadisticasDTO estadisticas) {
        actualizarEstadisticasDirecto(estadisticas);
    }

    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            areaLog.append("[" + ts + "] " + mensaje + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String titulo, String msg) {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public void actualizarCaja(int numCaja, String estado, int cola, String info) {
        panelCajas.actualizarCaja(numCaja, estado, cola, info);
    }

    public void actualizarCajaConTipo(int numCaja, String estado, int cola, String info, String tipo) {
        panelCajas.actualizarCaja(numCaja, estado, cola, info, tipo);
    }
}