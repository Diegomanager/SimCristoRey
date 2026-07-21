package com.supermercado.presentation.cooperativa.view;

import com.supermercado.application.cooperativa.dto.CalibracionMensual;
import com.supermercado.application.cooperativa.usecase.CalibrarConfiguracionUseCase;
import com.supermercado.domain.cooperativa.config.ConfiguracionCooperativa;
import com.supermercado.domain.cooperativa.model.ConfiguracionMultiServicio;
import com.supermercado.domain.cooperativa.model.JornadaLaboral;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.service.GeneradorSociosService;
import com.supermercado.infrastructure.adapter.export.HistorialImportadorAdapter;
import com.supermercado.infrastructure.adapter.preset.PresetManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class DialogoConfiguracionCooperativa extends JDialog {

    private final ConfiguracionCooperativa config;
    private final PanelCalendario panelCalendario = new PanelCalendario();

    private final JRadioButton rbLibre = new JRadioButton("Libre (Manual)", true);
    private final JRadioButton rbCalibrado = new JRadioButton("Calibrado (Desde Historial)");

    private final JSpinner spnHoras = new JSpinner(new SpinnerNumberModel(8.0, 1.0, 24.0, 0.5));
    private final JSpinner spnReal = new JSpinner(new SpinnerNumberModel(30, 5, 600, 5));
    private final JSpinner spnMaxSoc = new JSpinner(new SpinnerNumberModel(200, 10, 2000, 10));
    private final JSpinner spnIntv = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1));
    private final JLabel lblEscala = new JLabel();
    private final JLabel lblDias = new JLabel();

    private final JCheckBox chkPartida = new JCheckBox("Jornada partida", true);
    private final JSpinner spnHIni = spnMin(510);
    private final JSpinner spnHAlmFin = spnMin(720);
    private final JSpinner spnHRean = spnMin(870);
    private final JSpinner spnHFin = spnMin(990);
    private final JLabel lblHorario = new JLabel();

    private final Map<String, Object[]> slidersPorCodigo = new LinkedHashMap<>();
    private final JPanel panelProb = new JPanel(new GridBagLayout());
    private final JLabel lblSumaProb = new JLabel("Suma: 0%");

    private final JSpinner spnGen = new JSpinner(new SpinnerNumberModel(7, 0, 20, 1));
    private final JSpinner spnPlat = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
    private final JSpinner spnMM = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));
    private final JLabel lblTot = new JLabel();

    private final String[] COLS = {"ID", "Nombre", "C\u00f3digo", "Dur.Min", "Dur.Max", "Monto Min", "Monto Max", "Tasa%", "Activo"};
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLS, 0) {
        @Override
        public Class<?> getColumnClass(int c) {
            return switch (c) {
                case 3, 4 -> Integer.class;
                case 5, 6, 7 -> Double.class;
                case 8 -> Boolean.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return c != 0;
        }
    };

    private List<ServicioFinanciero> servicios;
    private CalibracionMensual ultimaCalibracion;
    private boolean modoCalibradoGuardado = false;
    private boolean confirmado = false;
    private long msPorMin = 62L;
    private ConfiguracionMultiServicio configMulti = new ConfiguracionMultiServicio();

    public DialogoConfiguracionCooperativa(Frame owner,
                                           List<ServicioFinanciero> svcsActuales,
                                           ConfiguracionCooperativa cfg) {
        super(owner, "\u2699  Configuraci\u00f3n \u2013 SimCristoRey", true);
        this.config = cfg;
        this.servicios = (svcsActuales != null && !svcsActuales.isEmpty())
                ? new ArrayList<>(svcsActuales)
                : crearServiciosPorDefecto();
        initUI();
        cargar();
        actualizarEscala();
        actualizarHorario();
        actualizarTotal();
        aplicarModo();
        setSize(1080, 800);
        setMinimumSize(new Dimension(920, 680));
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));

        ButtonGroup grupoModo = new ButtonGroup();
        grupoModo.add(rbLibre);
        grupoModo.add(rbCalibrado);
        rbLibre.addActionListener(e -> aplicarModo());
        rbCalibrado.addActionListener(e -> aplicarModo());

        JPanel panelModo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelModo.setBorder(BorderFactory.createTitledBorder("Modo de simulaci\u00f3n"));
        panelModo.add(rbLibre);
        panelModo.add(rbCalibrado);
        JLabel lblAyudaModo = new JLabel("<html><i>Calibrado: importa un historial y bloquea Calendario/Probabilidades/"
                + "M\u00e1x.socios/Intervalo (se calculan solos). Horario y Cajas se editan siempre.</i></html>");
        lblAyudaModo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        panelModo.add(lblAyudaModo);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("\u23F1 Velocidad", tabVelocidad());
        tabs.addTab("\uD83D\uDCC5 Calendario", panelCalendario);
        tabs.addTab("\uD83D\uDD70 Horario", tabHorario());
        tabs.addTab("\uD83D\uDCCA Probabilidades", tabProb());
        tabs.addTab("\uD83D\uDDBF Cajas", tabCajas());
        tabs.addTab("\uD83C\uDFE6 Servicios", tabServicios());

        JButton btnGuardarPreset = new JButton("\uD83D\uDCBE Guardar Config.");
        JButton btnCargarPreset = new JButton("\uD83D\uDCC2 Cargar Config.");
        btnGuardarPreset.addActionListener(e -> guardarPreset());
        btnCargarPreset.addActionListener(e -> cargarPreset());

        JButton ok = new JButton("Aceptar");
        JButton cnc = new JButton("Cancelar");
        ok.setFont(new Font("SansSerif", Font.BOLD, 12));
        ok.addActionListener(e -> {
            guardar();
            dispose();
        });
        cnc.addActionListener(e -> dispose());

        JPanel bot = new JPanel(new BorderLayout());
        JPanel botIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        botIzq.add(btnGuardarPreset);
        botIzq.add(btnCargarPreset);
        JPanel botDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        botDer.add(cnc);
        botDer.add(ok);
        bot.add(botIzq, BorderLayout.WEST);
        bot.add(botDer, BorderLayout.EAST);

        add(panelModo, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(bot, BorderLayout.SOUTH);
    }

    private void aplicarModo() {
        boolean calibrado = rbCalibrado.isSelected();
        setEnabledRecursivo(panelCalendario, !calibrado);
        spnMaxSoc.setEnabled(!calibrado);
        spnIntv.setEnabled(!calibrado);
        for (Object[] arr : slidersPorCodigo.values()) {
            ((JSlider) arr[0]).setEnabled(!calibrado);
        }
    }

    private void setEnabledRecursivo(Component comp, boolean enabled) {
        comp.setEnabled(enabled);
        if (comp instanceof Container) {
            for (Component hijo : ((Container) comp).getComponents()) {
                setEnabledRecursivo(hijo, enabled);
            }
        }
    }

    private JPanel tabVelocidad() {
        JPanel p = gbp();
        GridBagConstraints c = gbc();
        int r = 0;
        fila(p, c, r++, "Horas de jornada por d\u00eda:", spnHoras, "8.0 = 8 horas laborales");
        fila(p, c, r++, "Duraci\u00f3n real por d\u00eda (seg):", spnReal, "Segundos reales para un d\u00eda simulado");
        fila(p, c, r++, "Escala calculada:", lblEscala, "ms por minuto simulado (auto)");
        fila(p, c, r++, "M\u00e1x. socios por d\u00eda:", spnMaxSoc, "L\u00edmite de socios diario");
        fila(p, c, r++, "Intervalo llegada (min sim):", spnIntv, "Min simulados entre llegadas");
        fila(p, c, r++, "D\u00edas laborables:", lblDias, "Del calendario seleccionado");
        spnHoras.addChangeListener(e -> actualizarEscala());
        spnReal.addChangeListener(e -> actualizarEscala());
        return p;
    }

    private JPanel tabHorario() {
        JPanel p = gbp();
        GridBagConstraints c = gbc();
        int r = 0;
        GridBagConstraints ch = new GridBagConstraints();
        ch.gridx = 0;
        ch.gridy = r++;
        ch.gridwidth = 2;
        ch.insets = new Insets(0, 6, 10, 6);
        p.add(new JLabel("<html><b>Horario global para todos los d\u00edas laborables.</b></html>"), ch);
        fila(p, c, r++, "Inicio (ej. 510=08:30):", spnHIni, "Apertura en minutos desde 00:00");
        fila(p, c, r++, "", chkPartida, "Activar pausa de almuerzo");
        fila(p, c, r++, "Fin ma\u00f1ana (ej. 720=12:00):", spnHAlmFin, "Fin del primer bloque");
        fila(p, c, r++, "Reanudaci\u00f3n (ej. 870=14:30):", spnHRean, "Inicio del segundo bloque");
        fila(p, c, r++, "Cierre (ej. 990=16:30):", spnHFin, "Cierre");
        fila(p, c, r++, "Horario calculado:", lblHorario, "Resumen visual");
        for (JSpinner s : new JSpinner[]{spnHIni, spnHAlmFin, spnHRean, spnHFin})
            s.addChangeListener(e -> actualizarHorario());
        chkPartida.addActionListener(e -> actualizarHorario());
        JPanel w = new JPanel(new BorderLayout());
        w.add(p, BorderLayout.NORTH);
        return w;
    }

    private JPanel tabProb() {
        JPanel outer = new JPanel(new BorderLayout(4, 4));
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel instruccion = new JLabel("<html><b>Probabilidades por c\u00f3digo de ticket.</b> "
                + "La suma debe ser 100%.</html>");
        instruccion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        outer.add(instruccion, BorderLayout.NORTH);

        panelProb.setLayout(new GridBagLayout());
        JScrollPane scrollSliders = new JScrollPane(panelProb);
        scrollSliders.getVerticalScrollBar().setUnitIncrement(20);
        scrollSliders.setPreferredSize(new Dimension(380, 280));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        lblSumaProb.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottom.add(lblSumaProb);

        outer.add(scrollSliders, BorderLayout.CENTER);
        outer.add(bottom, BorderLayout.SOUTH);
        return outer;
    }

    private void reconstruirSliders() {
        panelProb.removeAll();
        slidersPorCodigo.clear();

        Map<String, Double> probs = config.getProbPorCodigo();
        if (probs.isEmpty()) {
            aplicarModo();
            return;
        }

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 0.3;
        int row = 0;

        List<String> orden = List.of("C", "A", "S", "F", "P", "R", "PC", "PS", "PA", "PP");
        List<String> claves = new ArrayList<>(probs.keySet());
        claves.sort((a, b) -> {
            int ia = orden.indexOf(a);
            int ib = orden.indexOf(b);
            if (ia >= 0 && ib >= 0) return Integer.compare(ia, ib);
            if (ia >= 0) return -1;
            if (ib >= 0) return 1;
            return a.compareTo(b);
        });

        for (String codigo : claves) {
            double valor = probs.getOrDefault(codigo, 0.0);
            int valInt = (int) Math.round(valor * 100);

            JLabel lblCodigo = new JLabel(codigo + " (" + GeneradorSociosService.getNombreCodigo(codigo) + ")");
            lblCodigo.setFont(new Font("SansSerif", Font.BOLD, 11));

            JSlider slider = new JSlider(0, 100, valInt);
            slider.setMajorTickSpacing(20);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);

            JLabel lblPorc = new JLabel(valInt + "%");
            lblPorc.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblPorc.setPreferredSize(new Dimension(45, 20));

            slider.addChangeListener(e -> {
                int v = slider.getValue();
                lblPorc.setText(v + "%");
                actualizarSuma();
            });

            c.gridy = row;
            c.gridx = 0;
            panelProb.add(lblCodigo, c);
            c.gridx = 1;
            c.weightx = 0.6;
            panelProb.add(slider, c);
            c.gridx = 2;
            c.weightx = 0.1;
            panelProb.add(lblPorc, c);
            row++;
            slidersPorCodigo.put(codigo, new Object[]{slider, lblPorc});
        }

        actualizarSuma();
        panelProb.revalidate();
        panelProb.repaint();
        aplicarModo();
    }

    private void actualizarSuma() {
        int suma = 0;
        for (Object[] arr : slidersPorCodigo.values()) {
            JSlider sl = (JSlider) arr[0];
            suma += sl.getValue();
        }
        lblSumaProb.setText("Suma: " + suma + "%  " + (suma == 100 ? "\u2705" : "\u26A0 Debe ser 100%"));
        lblSumaProb.setForeground(suma == 100 ? new Color(0, 120, 0) : Color.RED);
    }

    private JPanel tabCajas() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        outer.add(new JLabel("<html><b>Tipos de caja:</b> General, Plataforma, Montos Mayores (MM).<br>"
                + "Tickets P y PP solo van a PLATAFORMA. Montos \u2265 Bs 100.000 van a MM.</html>"));
        outer.add(Box.createVerticalStrut(12));
        outer.add(grupoCaja("\uD83C\uDFE6  Cajas Generales (G-XX)", "Atienden todos los tickets excepto P/PP.", new Color(210, 240, 210), spnGen));
        outer.add(Box.createVerticalStrut(8));
        outer.add(grupoCaja("\uD83D\uDDA5  Cajas de Plataforma (P-XX)", "Atienden P, PP y tambi\u00e9n otros tickets.", new Color(210, 225, 255), spnPlat));
        outer.add(Box.createVerticalStrut(8));
        outer.add(grupoCaja("\uD83D\uDCB0  Caja Montos Mayores (MM-XX)", "Para montos \u2265 Bs 100.000. Tambi\u00e9n atiende general.", new Color(255, 245, 200), spnMM));
        outer.add(Box.createVerticalStrut(12));
        lblTot.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTot.setForeground(new Color(0, 80, 160));
        lblTot.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(lblTot);
        for (JSpinner s : new JSpinner[]{spnGen, spnPlat, spnMM}) s.addChangeListener(e -> actualizarTotal());
        JScrollPane sc = new JScrollPane(outer);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        sc.setBorder(null);
        JPanel w = new JPanel(new BorderLayout());
        w.add(sc, BorderLayout.CENTER);
        return w;
    }

    private JPanel grupoCaja(String titulo, String desc, Color color, JSpinner spn) {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setBackground(color);
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lt = new JLabel(titulo);
        lt.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel ld = new JLabel("<html><i>" + desc + "</i></html>");
        ld.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ld.setForeground(Color.DARK_GRAY);
        JPanel iz = new JPanel(new BorderLayout(0, 4));
        iz.setOpaque(false);
        iz.add(lt, BorderLayout.NORTH);
        iz.add(ld, BorderLayout.CENTER);
        JPanel dr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        dr.setOpaque(false);
        dr.add(new JLabel("Cantidad:"));
        spn.setPreferredSize(new Dimension(70, 28));
        dr.add(spn);
        p.add(iz, BorderLayout.CENTER);
        p.add(dr, BorderLayout.EAST);
        return p;
    }

    private JPanel tabServicios() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.getColumnModel().getColumn(2).setMaxWidth(55);
        tabla.getColumnModel().getColumn(8).setMaxWidth(55);
        JComboBox<String> combo = new JComboBox<>(new String[]{"C", "A", "S", "F", "P", "R", "PC", "PS", "PA", "PP"});
        tabla.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(combo));
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getVerticalScrollBar().setUnitIncrement(20);
        scrollTabla.getHorizontalScrollBar().setUnitIncrement(20);

        JButton add = new JButton("+ Agregar");
        JButton del = new JButton("- Eliminar");
        JButton importar = new JButton("\uD83D\uDCC5 Importar desde Excel (Historial)");
        importar.setFont(new Font("SansSerif", Font.BOLD, 11));

        add.addActionListener(e -> {
            String nuevoCodigo = "NUEVO";
            modeloTabla.addRow(new Object[]{"SVC-" + (modeloTabla.getRowCount() + 1), "Nuevo", nuevoCodigo, 2, 10, 0.0, 500.0, 0.0, true});
            config.agregarCodigoNuevo(nuevoCodigo);
            reconstruirSliders();
        });
        del.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r >= 0) {
                String codigo = (String) modeloTabla.getValueAt(r, 2);
                modeloTabla.removeRow(r);
                boolean existe = false;
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (codigo.equals(modeloTabla.getValueAt(i, 2))) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    config.eliminarProbCodigo(codigo);
                    reconstruirSliders();
                }
            }
        });
        importar.addActionListener(e -> importarHistorial());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bar.add(add);
        bar.add(del);
        bar.add(new JLabel("  El c\u00f3digo determina el ticket y la caja destino."));
        JPanel barImport = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        barImport.add(importar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(bar, BorderLayout.WEST);
        sur.add(barImport, BorderLayout.EAST);

        p.add(scrollTabla, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private void importarHistorial() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Importar historial de atenciones");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File archivo = chooser.getSelectedFile();
        CalibrarConfiguracionUseCase useCase =
                new CalibrarConfiguracionUseCase(new HistorialImportadorAdapter());

        try {
            CalibracionMensual cal = useCase.ejecutar(archivo);
            ultimaCalibracion = cal;
            System.out.println(">>> [Dialogo] Calibraci\u00f3n guardada: " + cal.getRegistros().size() + " registros.");

            for (ServicioFinanciero s : cal.getServicios()) {
                int filaExistente = -1;
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (s.getTipoCajaRequerido().equals(modeloTabla.getValueAt(i, 2))) {
                        filaExistente = i;
                        break;
                    }
                }
                Object[] fila = {
                        s.getId(), s.getNombre(), s.getTipoCajaRequerido(),
                        s.getDuracionMinima(), s.getDuracionMaxima(),
                        s.getMontoMinimo(), s.getMontoMaximo(), s.getTasaInteres(), s.isActivo()
                };
                if (filaExistente >= 0) {
                    for (int col = 0; col < fila.length; col++) modeloTabla.setValueAt(fila[col], filaExistente, col);
                } else {
                    modeloTabla.addRow(fila);
                }
            }

            for (Map.Entry<String, Double> e : cal.getProbabilidades().entrySet()) {
                config.setProbCodigo(e.getKey(), e.getValue());
            }
            reconstruirSliders();

            boolean calibracionCompleta = false;
            if (cal.isTieneFechas()) {
                Map<LocalDate, Boolean> nuevoEstado = new LinkedHashMap<>();
                for (LocalDate d : cal.getDiasLaborables()) nuevoEstado.put(d, true);
                panelCalendario.setEstadoDias(nuevoEstado);
                // Actualizar también la configuración
                config.setDiasLaborables(new LinkedHashMap<>(nuevoEstado));
                config.setDiasASimular(nuevoEstado.size());

                spnMaxSoc.setValue(Math.max(10, cal.getMaxSociosDia()));
                double intervaloCalculado = Math.max(0.1,
                        Math.round(cal.getIntervaloPromedioLlegada() * 10) / 10.0);
                spnIntv.setValue(Math.min(10.0, intervaloCalculado));
                System.out.println(">>> [Calibracion] Intervalo aplicado: " + intervaloCalculado
                        + " min (calculado del historial: " + cal.getIntervaloPromedioLlegada() + ")");
                calibracionCompleta = true;
            }
            aplicarModo();
            actualizarDiasLabel();

            StringBuilder msg = new StringBuilder();
            msg.append("Calibraci\u00f3n completada.\n");
            msg.append("Fichas procesadas: ").append(cal.getTotalFichasProcesadas()).append("\n");
            msg.append("Filas ignoradas: ").append(cal.getFilasIgnoradas()).append("\n");
            msg.append("Tipos de servicio calibrados: ").append(cal.getServicios().size()).append("\n");
            if (rbCalibrado.isSelected()) {
                if (calibracionCompleta) {
                    msg.append("\nModo Calibrado: tambi\u00e9n se configuraron ")
                            .append(cal.getDiasLaborables().size()).append(" d\u00edas laborables, ")
                            .append("m\u00e1ximo de socios/d\u00eda (").append(cal.getMaxSociosDia()).append(") ")
                            .append("e intervalo de llegada promedio.");
                } else {
                    msg.append("\nAVISO: no se encontr\u00f3 la columna 'fecha_creacion' en el archivo, ")
                            .append("as\u00ed que NO se pudo calibrar calendario/m\u00e1x.socios/intervalo. ")
                            .append("Solo se calibraron servicios y probabilidades.");
                }
            }

            // Si hay fechas, activar automáticamente el modo calibrado
            if (cal.isTieneFechas() && !rbCalibrado.isSelected()) {
                rbCalibrado.setSelected(true);
                aplicarModo();
                System.out.println(">>> [Dialogo] Modo Calibrado activado autom\u00e1ticamente.");
            }

            JOptionPane.showMessageDialog(this, msg.toString(),
                    "Importaci\u00f3n completada", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo importar el historial:\n" + ex.getMessage(),
                    "Error de importaci\u00f3n", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarPreset() {
        File dirPresets = new File("config/presets");
        if (!dirPresets.exists()) dirPresets.mkdirs();

        JFileChooser chooser = new JFileChooser(dirPresets);
        chooser.setDialogTitle("Guardar configuraci\u00f3n (preset)");
        chooser.setFileFilter(new FileNameExtensionFilter("Preset (*.properties)", "properties"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".properties")) path += ".properties";
        File archivo = new File(path);

        Properties p = new Properties();
        p.setProperty("modo", rbCalibrado.isSelected() ? "CALIBRADO" : "LIBRE");
        p.setProperty("horasJornadaDia", String.valueOf(spnHoras.getValue()));
        p.setProperty("duracionRealSeg", String.valueOf(spnReal.getValue()));
        p.setProperty("maxSociosDia", String.valueOf(spnMaxSoc.getValue()));
        p.setProperty("intervaloMinutos", String.valueOf(spnIntv.getValue()));
        p.setProperty("horaInicio", String.valueOf(spnHIni.getValue()));
        p.setProperty("horaAlmuerzoFin", String.valueOf(spnHAlmFin.getValue()));
        p.setProperty("horaReanudacion", String.valueOf(spnHRean.getValue()));
        p.setProperty("horaFin", String.valueOf(spnHFin.getValue()));
        p.setProperty("jornadaPartida", String.valueOf(chkPartida.isSelected()));
        p.setProperty("cajasGenerales", String.valueOf(spnGen.getValue()));
        p.setProperty("cajasPlataforma", String.valueOf(spnPlat.getValue()));
        p.setProperty("cajasMM", String.valueOf(spnMM.getValue()));

        StringBuilder labs = new StringBuilder();
        StringBuilder nolabs = new StringBuilder();
        for (Map.Entry<LocalDate, Boolean> e : panelCalendario.getEstadoDias().entrySet()) {
            if (Boolean.TRUE.equals(e.getValue())) {
                if (labs.length() > 0) labs.append(",");
                labs.append(e.getKey());
            } else {
                if (nolabs.length() > 0) nolabs.append(",");
                nolabs.append(e.getKey());
            }
        }
        p.setProperty("diasLaborables", labs.toString());
        p.setProperty("diasNoLaborables", nolabs.toString());

        for (Map.Entry<String, Object[]> e : slidersPorCodigo.entrySet()) {
            JSlider sl = (JSlider) e.getValue()[0];
            p.setProperty("prob." + e.getKey(), String.valueOf(sl.getValue()));
        }

        p.setProperty("servicios.count", String.valueOf(modeloTabla.getRowCount()));
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String pre = "servicios." + i + ".";
            p.setProperty(pre + "id", String.valueOf(modeloTabla.getValueAt(i, 0)));
            p.setProperty(pre + "nombre", String.valueOf(modeloTabla.getValueAt(i, 1)));
            p.setProperty(pre + "codigo", String.valueOf(modeloTabla.getValueAt(i, 2)));
            p.setProperty(pre + "durMin", String.valueOf(modeloTabla.getValueAt(i, 3)));
            p.setProperty(pre + "durMax", String.valueOf(modeloTabla.getValueAt(i, 4)));
            p.setProperty(pre + "montoMin", String.valueOf(modeloTabla.getValueAt(i, 5)));
            p.setProperty(pre + "montoMax", String.valueOf(modeloTabla.getValueAt(i, 6)));
            p.setProperty(pre + "tasa", String.valueOf(modeloTabla.getValueAt(i, 7)));
            p.setProperty(pre + "activo", String.valueOf(modeloTabla.getValueAt(i, 8)));
        }

        try {
            PresetManager.guardar(archivo, p);
            JOptionPane.showMessageDialog(this,
                    "Configuraci\u00f3n guardada en:\n" + archivo.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPreset() {
        File dirPresets = new File("config/presets");
        JFileChooser chooser = new JFileChooser(dirPresets.exists() ? dirPresets : new File("."));
        chooser.setDialogTitle("Cargar configuraci\u00f3n (preset)");
        chooser.setFileFilter(new FileNameExtensionFilter("Preset (*.properties)", "properties"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try {
            Properties p = PresetManager.cargar(chooser.getSelectedFile());

            if ("CALIBRADO".equals(p.getProperty("modo"))) rbCalibrado.setSelected(true);
            else rbLibre.setSelected(true);

            setSpinnerDouble(spnHoras, p.getProperty("horasJornadaDia"));
            setSpinnerInt(spnReal, p.getProperty("duracionRealSeg"));
            setSpinnerInt(spnMaxSoc, p.getProperty("maxSociosDia"));
            setSpinnerDouble(spnIntv, p.getProperty("intervaloMinutos"));
            setSpinnerInt(spnHIni, p.getProperty("horaInicio"));
            setSpinnerInt(spnHAlmFin, p.getProperty("horaAlmuerzoFin"));
            setSpinnerInt(spnHRean, p.getProperty("horaReanudacion"));
            setSpinnerInt(spnHFin, p.getProperty("horaFin"));
            chkPartida.setSelected(Boolean.parseBoolean(p.getProperty("jornadaPartida", "true")));
            setSpinnerInt(spnGen, p.getProperty("cajasGenerales"));
            setSpinnerInt(spnPlat, p.getProperty("cajasPlataforma"));
            setSpinnerInt(spnMM, p.getProperty("cajasMM"));

            Map<LocalDate, Boolean> estadoDias = new LinkedHashMap<>();
            for (String s : split(p.getProperty("diasLaborables", ""))) {
                try {
                    estadoDias.put(LocalDate.parse(s), true);
                } catch (Exception ignored) {
                }
            }
            for (String s : split(p.getProperty("diasNoLaborables", ""))) {
                try {
                    estadoDias.put(LocalDate.parse(s), false);
                } catch (Exception ignored) {
                }
            }
            if (!estadoDias.isEmpty()) panelCalendario.setEstadoDias(estadoDias);

            for (String key : p.stringPropertyNames()) {
                if (key.startsWith("prob.")) {
                    String codigo = key.substring("prob.".length());
                    try {
                        double v = Double.parseDouble(p.getProperty(key)) / 100.0;
                        config.setProbCodigo(codigo, v);
                    } catch (Exception ignored) {
                    }
                }
            }

            int nServicios = 0;
            try {
                nServicios = Integer.parseInt(p.getProperty("servicios.count", "0"));
            } catch (Exception ignored) {
            }
            if (nServicios > 0) {
                modeloTabla.setRowCount(0);
                for (int i = 0; i < nServicios; i++) {
                    String pre = "servicios." + i + ".";
                    try {
                        modeloTabla.addRow(new Object[]{
                                p.getProperty(pre + "id", "SVC-" + i),
                                p.getProperty(pre + "nombre", ""),
                                p.getProperty(pre + "codigo", "C"),
                                Integer.parseInt(p.getProperty(pre + "durMin", "2")),
                                Integer.parseInt(p.getProperty(pre + "durMax", "10")),
                                Double.parseDouble(p.getProperty(pre + "montoMin", "0")),
                                Double.parseDouble(p.getProperty(pre + "montoMax", "500")),
                                Double.parseDouble(p.getProperty(pre + "tasa", "0.1")),
                                Boolean.parseBoolean(p.getProperty(pre + "activo", "true"))
                        });
                    } catch (Exception ignored) {
                    }
                }
            }

            reconstruirSliders();
            actualizarEscala();
            actualizarHorario();
            actualizarTotal();
            actualizarDiasLabel();
            aplicarModo();

            JOptionPane.showMessageDialog(this, "Configuraci\u00f3n cargada correctamente.");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSpinnerDouble(JSpinner spn, String valor) {
        if (valor == null) return;
        try {
            spn.setValue(Double.parseDouble(valor));
        } catch (Exception ignored) {
        }
    }

    private void setSpinnerInt(JSpinner spn, String valor) {
        if (valor == null) return;
        try {
            spn.setValue((int) Double.parseDouble(valor));
        } catch (Exception ignored) {
        }
    }

    private List<String> split(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null || csv.isBlank()) return out;
        for (String s : csv.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private void cargar() {
        spnHoras.setValue(config.getHorasJornadaDia());
        int minDia = (int) (config.getHorasJornadaDia() * 60);
        long ms = config.getMsPorMinuto();
        int seg = minDia > 0 ? (int) (ms * minDia / 1000L) : 30;
        spnReal.setValue(Math.max(5, Math.min(seg, 600)));
        spnMaxSoc.setValue(config.getMaxSociosDia());
        spnIntv.setValue(config.getIntervaloMinutos());
        spnHIni.setValue(config.getHoraInicio());
        spnHAlmFin.setValue(config.getHoraAlmuerzoFin());
        spnHRean.setValue(config.getHoraReanudacion());
        spnHFin.setValue(config.getHoraFin());
        chkPartida.setSelected(config.isJornadaPartida());
        spnGen.setValue(config.getCajasGenerales());
        spnPlat.setValue(config.getCajasPlataforma());
        spnMM.setValue(config.getCajasMM());

        Map<LocalDate, Boolean> dias = config.getDiasLaborables();
        if (dias != null && !dias.isEmpty()) panelCalendario.setEstadoDias(dias);

        modeloTabla.setRowCount(0);
        for (ServicioFinanciero s : servicios)
            modeloTabla.addRow(new Object[]{s.getId(), s.getNombre(), s.getTipoCajaRequerido(),
                    s.getDuracionMinima(), s.getDuracionMaxima(),
                    s.getMontoMinimo(), s.getMontoMaximo(), s.getTasaInteres(), s.isActivo()});

        reconstruirSliders();
        actualizarDiasLabel();
    }

    private void guardar() {
        double horas = (double) spnHoras.getValue();
        int real = (int) spnReal.getValue();
        int minDia = (int) (horas * 60);
        msPorMin = (minDia > 0 && real > 0) ? Math.max(1L, (long) real * 1000L / minDia) : 62L;

        config.setHorasJornadaDia(horas);
        config.setMsPorMinuto(msPorMin);
        config.setMaxSociosDia((int) spnMaxSoc.getValue());
        config.setIntervaloMinutos((double) spnIntv.getValue());
        config.setHoraInicio((int) spnHIni.getValue());
        config.setHoraAlmuerzoFin((int) spnHAlmFin.getValue());
        config.setHoraReanudacion((int) spnHRean.getValue());
        config.setHoraFin((int) spnHFin.getValue());
        config.setJornadaPartida(chkPartida.isSelected());
        config.setCajasGenerales((int) spnGen.getValue());
        config.setCajasPlataforma((int) spnPlat.getValue());
        config.setCajasMM((int) spnMM.getValue());

        for (Map.Entry<String, Object[]> entry : slidersPorCodigo.entrySet()) {
            JSlider sl = (JSlider) entry.getValue()[0];
            double prob = sl.getValue() / 100.0;
            config.setProbCodigo(entry.getKey(), prob);
        }
        config.normalizarProbabilidades();

        config.setDiasLaborables(new LinkedHashMap<>(panelCalendario.getEstadoDias()));
        config.setDiasASimular(Math.max(1, panelCalendario.getDiasLaborables()));

        servicios.clear();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            ServicioFinanciero s = new ServicioFinanciero();
            s.setId((String) modeloTabla.getValueAt(i, 0));
            s.setNombre((String) modeloTabla.getValueAt(i, 1));
            s.setTipoCajaRequerido((String) modeloTabla.getValueAt(i, 2));
            s.setDuracionMinima((Integer) modeloTabla.getValueAt(i, 3));
            s.setDuracionMaxima((Integer) modeloTabla.getValueAt(i, 4));
            s.setMontoMinimo((Double) modeloTabla.getValueAt(i, 5));
            s.setMontoMaximo((Double) modeloTabla.getValueAt(i, 6));
            s.setTasaInteres((Double) modeloTabla.getValueAt(i, 7));
            s.setActivo((Boolean) modeloTabla.getValueAt(i, 8));
            servicios.add(s);
        }
        configMulti = new ConfiguracionMultiServicio(0.25, 4, 3);
        config.guardar();
        confirmado = true;
        modoCalibradoGuardado = rbCalibrado.isSelected();
    }

    private void actualizarEscala() {
        double h = (double) spnHoras.getValue();
        int r = (int) spnReal.getValue();
        int m = (int) (h * 60);
        long ms = m > 0 && r > 0 ? Math.max(1L, (long) r * 1000L / m) : 62L;
        lblEscala.setText(ms + " ms/min \u2192 " + h + "h en " + r + " seg reales");
        lblEscala.setForeground(ms < 20 ? Color.RED : new Color(0, 110, 0));
        actualizarDiasLabel();
    }

    private void actualizarDiasLabel() {
        int d = panelCalendario.getDiasLaborables();
        lblDias.setText(d + " d\u00edas seleccionados");
        lblDias.setForeground(d == 0 ? Color.RED : new Color(0, 100, 0));
    }

    private void actualizarHorario() {
        int ini = (int) spnHIni.getValue(), af = (int) spnHAlmFin.getValue();
        int re = (int) spnHRean.getValue(), fin = (int) spnHFin.getValue();
        boolean pt = chkPartida.isSelected();
        lblHorario.setText(pt ? String.format("%02d:%02d\u2013%02d:%02d \u2615 %02d:%02d\u2013%02d:%02d | %d min",
                ini / 60, ini % 60, af / 60, af % 60, re / 60, re % 60, fin / 60, fin % 60, (af - ini) + (fin - re))
                : String.format("%02d:%02d\u2013%02d:%02d | %d min", ini / 60, ini % 60, fin / 60, fin % 60, fin - ini));
        lblHorario.setForeground(new Color(0, 100, 0));
    }

    private void actualizarTotal() {
        int t = (int) spnGen.getValue() + (int) spnPlat.getValue() + (int) spnMM.getValue();
        lblTot.setText("Total de cajas: " + t);
    }

    private List<ServicioFinanciero> crearServiciosPorDefecto() {
        List<ServicioFinanciero> l = new ArrayList<>();
        l.add(svc("SVC-C1", "Socios Ahorro/Cr\u00e9dito", "C", 3, 15, 500, 200000, 0.0));
        l.add(svc("SVC-A1", "Socios Semapa (Agua)", "A", 2, 12, 30, 200, 0.0));
        l.add(svc("SVC-S1", "Socios Elfec-Comteco-Semapa", "S", 2, 15, 20, 300, 0.0));
        l.add(svc("SVC-F1", "Socios Fraccionamiento", "F", 4, 30, 200, 5000, 0.0));
        l.add(svc("SVC-P1", "Socios Plataforma", "P", 1, 10, 50, 500, 0.0));
        l.add(svc("SVC-R1", "Renta Dignidad", "R", 2, 15, 250, 250, 0.0));
        l.add(svc("SVC-PC1", "Preferente Ahorro-Cr\u00e9dito", "PC", 1, 8, 500, 200000, 0.0));
        l.add(svc("SVC-PS1", "Preferente Elfec-Comteco-Semapa", "PS", 1, 8, 20, 300, 0.0));
        l.add(svc("SVC-PA1", "Preferente Semapa", "PA", 1, 8, 30, 200, 0.0));
        l.add(svc("SVC-PP1", "Preferente Plataforma", "PP", 1, 8, 50, 500, 0.0));
        l.forEach(s -> s.setActivo(true));
        return l;
    }

    private ServicioFinanciero svc(String id, String n, String t, int dm, int dx, double mm, double mx, double ta) {
        return new ServicioFinanciero(id, n, t, dm, dx, mm, mx, ta, 0.1);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public long getMsPorMinuto() {
        return msPorMin;
    }

    public ConfiguracionMultiServicio getConfigMulti() {
        return configMulti;
    }

    public List<ServicioFinanciero> getServicios() {
        return servicios;
    }

    public ConfiguracionCooperativa getConfig() {
        return config;
    }

    public PanelCalendario getCalendario() {
        return panelCalendario;
    }

    public CalibracionMensual getUltimaCalibracion() {
        System.out.println(">>> [Dialogo] getUltimaCalibracion() -> " +
                (ultimaCalibracion != null ? ultimaCalibracion.getRegistros().size() + " registros" : "null"));
        return ultimaCalibracion;
    }

    public boolean isModoCalibradoGuardado() {
        return modoCalibradoGuardado;
    }

    public List<JornadaLaboral> getJornadas() {
        return panelCalendario.generarJornadas(
                (int) spnHIni.getValue(), (int) spnHAlmFin.getValue(),
                (int) spnHRean.getValue(), (int) spnHFin.getValue(),
                chkPartida.isSelected());
    }

    private static JPanel gbp() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return p;
    }

    private static GridBagConstraints gbc() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private static void fila(JPanel p, GridBagConstraints c, int r, String l, JComponent comp, String tip) {
        c.gridx = 0;
        c.gridy = r;
        c.weightx = 0.42;
        p.add(new JLabel(l), c);
        c.gridx = 1;
        c.weightx = 0.58;
        comp.setToolTipText(tip);
        p.add(comp, c);
    }

    private static JSpinner spnMin(int v) {
        return new JSpinner(new SpinnerNumberModel(v, 0, 1439, 30));
    }
}