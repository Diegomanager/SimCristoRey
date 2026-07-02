package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.event.EventoSimulacion;
import com.supermercado.domain.cooperativa.event.TipoEvento;
import com.supermercado.domain.cooperativa.model.*;
import com.supermercado.domain.cooperativa.service.SimuladorCooperativaService;
import com.supermercado.presentation.cooperativa.panel.PanelSalaEspera;
import com.supermercado.presentation.cooperativa.panel.PanelVentanillas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class SimuladorCooperativaFrame extends JFrame {

    private final SimuladorCooperativaService simulador = new SimuladorCooperativaService();

    private final PanelSalaEspera              panelSala       = new PanelSalaEspera();
    private final PanelVentanillas             panelVentanillas= new PanelVentanillas();
    private final PanelEstadisticasFinancieras panelStats      = new PanelEstadisticasFinancieras();
    private final PanelLog                     panelLog        = new PanelLog();

    private final JButton btnIniciar    = new JButton("Iniciar");
    private final JButton btnPausar     = new JButton("Pausar");
    private final JButton btnReanudar   = new JButton("Reanudar");
    private final JButton btnDetener    = new JButton("Detener");
    private final JButton btnReiniciar  = new JButton("Reiniciar");
    private final JButton btnConfig     = new JButton("Config");
    private final JLabel  lblEstado     = new JLabel("Estado: En espera", SwingConstants.CENTER);
    private final JLabel  lblTiempo     = new JLabel("Tiempo: 0 min", SwingConstants.CENTER);

    private final Timer timerUI = new Timer(500, e -> refrescarUI());

    public SimuladorCooperativaFrame() {
        super("SimCristoRey – Cooperativa de Ahorro y Prestamo");
        initUI();
        conectarSimulador();
        configurarVentana();
        timerUI.start();
    }

    private void initUI() {
        setLayout(new BorderLayout(4, 4));

        JPanel barraControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barraControl.add(btnConfig);
        barraControl.add(new JSeparator(SwingConstants.VERTICAL));
        barraControl.add(btnIniciar);
        barraControl.add(btnPausar);
        barraControl.add(btnReanudar);
        barraControl.add(btnDetener);
        barraControl.add(btnReiniciar);
        barraControl.add(new JSeparator(SwingConstants.VERTICAL));
        barraControl.add(lblEstado);
        barraControl.add(lblTiempo);
        add(barraControl, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sala de Espera",   panelSala);
        tabs.addTab("Ventanillas",       panelVentanillas);
        tabs.addTab("Estadisticas",      panelStats);
        tabs.addTab("Log de Eventos",    panelLog);
        add(tabs, BorderLayout.CENTER);

        btnPausar.setEnabled(false);
        btnReanudar.setEnabled(false);
        btnDetener.setEnabled(false);
        btnReiniciar.setEnabled(false);

        btnConfig.addActionListener(e -> abrirConfiguracion());
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnPausar.addActionListener(e -> simulador.pausar());
        btnReanudar.addActionListener(e -> simulador.reanudar());
        btnDetener.addActionListener(e -> simulador.detener());
        btnReiniciar.addActionListener(e -> {
            simulador.reiniciar();
            panelSala.limpiar();
            panelVentanillas.limpiar();
            panelLog.limpiar();
            btnIniciar.setEnabled(true);
            btnPausar.setEnabled(false);
            btnReanudar.setEnabled(false);
            btnDetener.setEnabled(false);
            lblEstado.setText("Estado: En espera");
        });
    }

    private void conectarSimulador() {
        simulador.addListener(this::manejarEvento);

        simulador.configurar(480, SimuladorCooperativaService.calcularEscala(480, 30),
                400, 1.0, serviciosPorDefecto(), cajasPorDefecto());
        panelVentanillas.setCajas(simulador.getCajas());
    }

    private void manejarEvento(EventoSimulacion evento) {
        panelLog.addEvento(evento);

        SwingUtilities.invokeLater(() -> {
            switch (evento.getTipo()) {
                case SIMULACION_INICIADA -> {
                    lblEstado.setText("Estado: Corriendo");
                    lblEstado.setForeground(new Color(0, 120, 0));
                    btnIniciar.setEnabled(false);
                    btnPausar.setEnabled(true);
                    btnReanudar.setEnabled(false);
                    btnDetener.setEnabled(true);
                    btnReiniciar.setEnabled(false);
                }
                case SIMULACION_PAUSADA -> {
                    lblEstado.setText("Estado: Pausado");
                    lblEstado.setForeground(Color.ORANGE);
                    btnPausar.setEnabled(false);
                    btnReanudar.setEnabled(true);
                }
                case SIMULACION_REANUDADA -> {
                    lblEstado.setText("Estado: Corriendo");
                    lblEstado.setForeground(new Color(0, 120, 0));
                    btnPausar.setEnabled(true);
                    btnReanudar.setEnabled(false);
                }
                case FASE_PRINCIPAL_FINALIZADA -> {
                    lblEstado.setText("Estado: Fase principal finalizada");
                    lblEstado.setForeground(new Color(0, 0, 180));
                    btnPausar.setEnabled(false);
                    btnReanudar.setEnabled(false);
                    btnDetener.setEnabled(false);

                    // Preguntar si iniciar fase de rezagados
                    int sociosEnSala = simulador.getSalaEspera().getTotalEsperando();
                    if (sociosEnSala > 0) {
                        int respuesta = JOptionPane.showConfirmDialog(
                                this,
                                "Hay " + sociosEnSala + " socios en la sala de espera.\n" +
                                "¿Desea iniciar la fase de rezagados para atenderlos?",
                                "Fase de Rezagados",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (respuesta == JOptionPane.YES_OPTION) {
                            simulador.iniciarFaseRezagados();
                            lblEstado.setText("Estado: Rezagados en progreso...");
                            lblEstado.setForeground(new Color(180, 100, 0));
                        } else {
                            // Finalizar simulación sin atender rezagados
                            simulador.detener();
                            // Mostrar estadísticas finales con rezagados
                            mostrarEstadisticasFinales();
                        }
                    } else {
                        // No hay socios en sala, finalizar automáticamente
                        JOptionPane.showMessageDialog(this,
                                "No hay socios en sala. La simulación ha finalizado.",
                                "Simulación finalizada",
                                JOptionPane.INFORMATION_MESSAGE);
                        simulador.detener();
                    }
                }
                case FASE_REZAGADOS_INICIADA -> {
                    lblEstado.setText("Estado: Rezagados en progreso...");
                    lblEstado.setForeground(new Color(180, 100, 0));
                }
                case SIMULACION_FINALIZADA -> {
                    lblEstado.setText("Estado: Finalizado");
                    lblEstado.setForeground(new Color(0, 0, 180));
                    btnIniciar.setEnabled(false);
                    btnPausar.setEnabled(false);
                    btnReanudar.setEnabled(false);
                    btnDetener.setEnabled(false);
                    btnReiniciar.setEnabled(true);
                    mostrarEstadisticasFinales();
                }
                case SIMULACION_DETENIDA -> {
                    lblEstado.setText("Estado: Detenido");
                    lblEstado.setForeground(Color.DARK_GRAY);
                    btnIniciar.setEnabled(true);
                    btnPausar.setEnabled(false);
                    btnReanudar.setEnabled(false);
                    btnDetener.setEnabled(false);
                    btnReiniciar.setEnabled(true);
                }
                case SIMULACION_REINICIADA -> {
                    lblEstado.setText("Estado: Reiniciado");
                    lblEstado.setForeground(Color.DARK_GRAY);
                    btnIniciar.setEnabled(true);
                    btnPausar.setEnabled(false);
                    btnReanudar.setEnabled(false);
                    btnDetener.setEnabled(false);
                    btnReiniciar.setEnabled(false);
                }
                default -> {}
            }
        });
    }

    private void mostrarEstadisticasFinales() {
        // Implementar diálogo con estadísticas finales (puede ser un panel emergente)
        // Por ahora mostramos un mensaje simple
        int atendidos = simulador.getEstadisticas().getTotalAtendidos();
        int generados = simulador.getGenerador().getTotalGenerados();
        int rezagados = generados - atendidos;
        double monto = simulador.getEstadisticas().getMontoTotal();
        String estrella = simulador.getEstadisticas().getCajeroEstrella();

        JOptionPane.showMessageDialog(this,
            "=== ESTADISTICAS FINALES ===\n" +
            "Socios generados: " + generados + "\n" +
            "Socios atendidos: " + atendidos + "\n" +
            "Socios rezagados: " + rezagados + "\n" +
            "Monto total: Bs " + String.format("%.2f", monto) + "\n" +
            "Cajero Estrella: " + estrella,
            "Estadisticas Finales",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void refrescarUI() {
        if (!simulador.isCorriendo() && !simulador.isFaseRezagados() && !simulador.isFasePrincipalFinalizada()) {
            // Si no está corriendo ni en rezagados, no actualizar
            return;
        }

        lblTiempo.setText("Tiempo: " + simulador.getTiempoSimulado() + " min sim");

        if (simulador.getSalaEspera() != null)
            panelSala.actualizar(simulador.getSalaEspera());

        if (simulador.getCajas() != null)
            panelVentanillas.actualizar(simulador.getCajas());

        panelStats.actualizar(
                simulador.getEstadisticas(),
                simulador.getGenerador().getTotalGenerados());
    }

    private void abrirConfiguracion() {
        if (simulador.isCorriendo() || simulador.isFasePrincipalFinalizada()) {
            JOptionPane.showMessageDialog(this,
                    "Deten la simulacion antes de cambiar la configuracion.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DialogoConfiguracionCooperativa dlg = new DialogoConfiguracionCooperativa(this);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            simulador.configurar(
                    dlg.getDuracionSimulada(), dlg.getMsPorMinuto(),
                    dlg.getMaxSocios(), dlg.getIntervaloLlegada(),
                    serviciosPorDefecto(), cajasPorDefecto());
            panelVentanillas.setCajas(simulador.getCajas());
            panelLog.limpiar();
        }
    }

    private void iniciarSimulacion() {
        panelSala.limpiar();
        panelLog.limpiar();
        simulador.iniciar();
    }

    // ── Datos por defecto ─────────────────────────────────────────────────────
    private List<ServicioFinanciero> serviciosPorDefecto() {
        List<ServicioFinanciero> lista = new ArrayList<>();

        lista.add(crearServicio("SVC-GEN1", "Consulta de Saldo",     "GENERAL",  2, 5,   0,    500,  0.0,   0.25));
        lista.add(crearServicio("SVC-GEN2", "Pago de Servicios",     "GENERAL",  3, 8,  20,    500,  0.0,   0.20));
        lista.add(crearServicio("SVC-GEN3", "Transferencia General", "GENERAL",  4, 10, 100,  5000,  0.0,   0.20));
        lista.add(crearServicio("SVC-CRE1", "Prestamo Personal",     "CREDITOS", 10,25,1000,50000, 12.0,   0.20));
        lista.add(crearServicio("SVC-REC1", "Reclamo / Queja",       "RECLAMO",  8, 20,   0,    0,   0.0,   0.05));
        lista.add(crearServicio("SVC-AHO1", "Deposito de Ahorro",    "AHORRO",   3, 8,  50,  5000,  3.5,   0.05));
        lista.add(crearServicio("SVC-MM1",  "Pago Movil",            "MM",       2, 5,   5,   500,  0.0,   0.05));

        return lista;
    }

    private ServicioFinanciero crearServicio(String id, String nombre, String tipoCaja,
            int durMin, int durMax, double monMin, double monMax,
            double tasa, double prob) {
        ServicioFinanciero s = new ServicioFinanciero(id, nombre);
        s.setTipoCajaRequerido(tipoCaja);
        s.setDuracionMinima(durMin);
        s.setDuracionMaxima(durMax);
        s.setMontoMinimo(monMin);
        s.setMontoMaximo(monMax);
        s.setTasaInteres(tasa);
        s.setProbabilidad(prob);
        s.setActivo(true);
        return s;
    }

    private List<Caja> cajasPorDefecto() {
        List<Caja> cajas = new ArrayList<>();
        cajas.add(crearCaja("C-001", "GENERAL",  "Ventanilla General 1"));
        cajas.add(crearCaja("C-002", "GENERAL",  "Ventanilla General 2"));
        cajas.add(crearCaja("C-003", "GENERAL",  "Ventanilla General 3"));
        cajas.add(crearCaja("C-004", "CREDITOS", "Asesor Creditos 1"));
        cajas.add(crearCaja("C-005", "RECLAMO",  "Punto de Reclamo"));
        cajas.add(crearCaja("C-006", "AHORRO",   "Punto de Ahorro"));
        cajas.add(crearCaja("C-007", "MM",       "Modulo Movil"));
        return cajas;
    }

    private Caja crearCaja(String id, String tipoId, String nombre) {
        TipoCaja tipo = new TipoCaja(tipoId, nombre, tipoId.substring(0, Math.min(3, tipoId.length())));
        tipo.setFactorVelocidad(1.0);
        tipo.setPrioridad(tipoId.equals("GENERAL") ? 3 : 1);
        tipo.setActivo(true);
        Caja caja = new Caja(id, tipo);
        caja.setActiva(true);
        caja.setEstado(EstadoCaja.LIBRE);
        return caja;
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (simulador.isCorriendo() || simulador.isFasePrincipalFinalizada()) {
                    int confirm = JOptionPane.showConfirmDialog(
                            SimuladorCooperativaFrame.this,
                            "La simulación está en progreso. ¿Desea detenerla y salir?",
                            "Salir",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm != JOptionPane.YES_OPTION) return;
                    simulador.detener();
                }
                timerUI.stop();
                dispose();
            }
        });
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }
}