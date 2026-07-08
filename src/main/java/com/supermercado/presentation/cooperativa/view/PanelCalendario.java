package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.model.JornadaLaboral;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Calendario visual con colores de fondo completo en cada botón.
 * Estado persistido a través de getEstadoDias() / setEstadoDias().
 */
public class PanelCalendario extends JPanel {

    private int anioActual = LocalDate.now().getYear();
    private int mesActual  = LocalDate.now().getMonthValue();

    private final Map<LocalDate, Boolean> estadoDias = new LinkedHashMap<>();

    private final JSpinner           spnAnio  = new JSpinner(new SpinnerNumberModel(anioActual, 2020, 2050, 1));
    private final JComboBox<String>  cmbMes   = new JComboBox<>(new String[]{
        "Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
    });
    private final JPanel  panelDias  = new JPanel();
    private final JLabel  lblResumen = new JLabel("Días laborables: 0");

    // Colores con fondo completo visible
    private static final Color C_LABORABLE    = new Color(100, 200, 100);  // verde
    private static final Color C_NO_LABORABLE = new Color(230, 100, 100);  // rojo
    private static final Color C_SABADO_LAB   = new Color(100, 150, 220);  // azul laborable
    private static final Color C_SABADO_NOLAB = new Color(160, 180, 230);  // azul no laborable
    private static final Color C_DOMINGO_LAB  = new Color(200, 150, 200);  // violeta laborable
    private static final Color C_DOMINGO_NOLAB= new Color(190, 190, 190);  // gris no laborable
    private static final Color C_HOY          = new Color(255, 220, 50);   // amarillo
    private static final Color C_CABECERA     = new Color(60, 90, 140);

    public PanelCalendario() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Controles de navegación
        JButton btnPrev    = new JButton("◀");
        JButton btnNext    = new JButton("▶");
        JButton btnTodo    = new JButton("✔ Todo laborable");
        JButton btnNada    = new JButton("✖ Todo libre");
        JButton btnDefecto = new JButton("↺ Restablecer");

        btnPrev.addActionListener(e -> navegarMes(-1));
        btnNext.addActionListener(e -> navegarMes(1));
        btnTodo.addActionListener(e -> marcarTodo(true));
        btnNada.addActionListener(e -> marcarTodo(false));
        btnDefecto.addActionListener(e -> aplicarDefectos());

        spnAnio.addChangeListener(e -> {
            anioActual = (int) spnAnio.getValue();
            refrescarCalendario();
        });
        cmbMes.setSelectedIndex(mesActual - 1);
        cmbMes.addActionListener(e -> {
            mesActual = cmbMes.getSelectedIndex() + 1;
            refrescarCalendario();
        });

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        nav.add(btnPrev);
        nav.add(new JLabel("Año:")); nav.add(spnAnio);
        nav.add(new JLabel("Mes:")); nav.add(cmbMes);
        nav.add(btnNext);
        nav.add(Box.createHorizontalStrut(10));
        nav.add(btnDefecto); nav.add(btnTodo); nav.add(btnNada);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        leyenda.add(cuadro(C_LABORABLE,     "Laborable"));
        leyenda.add(cuadro(C_NO_LABORABLE,  "No laborable"));
        leyenda.add(cuadro(C_SABADO_LAB,    "Sábado lab."));
        leyenda.add(cuadro(C_DOMINGO_NOLAB, "Domingo libre"));
        leyenda.add(cuadro(C_HOY,           "Hoy"));
        lblResumen.setFont(new Font("SansSerif", Font.BOLD, 12));
        leyenda.add(lblResumen);

        JPanel top = new JPanel(new BorderLayout());
        top.add(nav,    BorderLayout.CENTER);
        top.add(leyenda,BorderLayout.SOUTH);

        panelDias.setLayout(new GridLayout(0, 7, 2, 2));

        add(top,       BorderLayout.NORTH);
        add(panelDias, BorderLayout.CENTER);

        refrescarCalendario();
    }

    private void navegarMes(int delta) {
        mesActual += delta;
        if (mesActual < 1)  { mesActual = 12; anioActual--; spnAnio.setValue(anioActual); }
        if (mesActual > 12) { mesActual = 1;  anioActual++; spnAnio.setValue(anioActual); }
        cmbMes.setSelectedIndex(mesActual - 1);
        refrescarCalendario();
    }

    /** Público para permitir refrescar desde fuera. */
    public void refrescarCalendario() {
        panelDias.removeAll();

        // Cabeceras días semana
        String[] cabeceras = {"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"};
        for (String cab : cabeceras) {
            JLabel lbl = new JLabel(cab, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setOpaque(true);
            lbl.setBackground(C_CABECERA);
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            panelDias.add(lbl);
        }

        LocalDate primerDia = LocalDate.of(anioActual, mesActual, 1);
        LocalDate hoy       = LocalDate.now();
        int diasEnMes       = primerDia.lengthOfMonth();

        // Relleno inicial (lunes=1 en DayOfWeek, queremos col 0)
        int offset = primerDia.getDayOfWeek().getValue() - 1;
        for (int i = 0; i < offset; i++) panelDias.add(celdaVacia());

        // Inicializar días que aún no tienen estado
        for (int d = 1; d <= diasEnMes; d++) {
            LocalDate f = LocalDate.of(anioActual, mesActual, d);
            if (!estadoDias.containsKey(f)) {
                // Lun-Sáb laborable, Dom libre
                estadoDias.put(f, f.getDayOfWeek() != DayOfWeek.SUNDAY);
            }
        }

        // Botones de días
        for (int d = 1; d <= diasEnMes; d++) {
            final LocalDate fecha = LocalDate.of(anioActual, mesActual, d);
            boolean laborable = estadoDias.getOrDefault(fecha, true);
            DayOfWeek dow     = fecha.getDayOfWeek();
            boolean esDom     = dow == DayOfWeek.SUNDAY;
            boolean esSab     = dow == DayOfWeek.SATURDAY;
            boolean esHoy     = fecha.equals(hoy);

            // Color de fondo según estado y tipo de día
            Color bg;
            if (esHoy) {
                bg = C_HOY;
            } else if (esDom) {
                bg = laborable ? C_DOMINGO_LAB : C_DOMINGO_NOLAB;
            } else if (esSab) {
                bg = laborable ? C_SABADO_LAB  : C_SABADO_NOLAB;
            } else {
                bg = laborable ? C_LABORABLE   : C_NO_LABORABLE;
            }

            JButton btn = new JButton(String.valueOf(d));
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);  // ← clave para fondo completo
            btn.setBackground(bg);
            btn.setForeground(esHoy ? Color.BLACK : Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(3, 3, 3, 3));

            String nombreDia = dow.getDisplayName(TextStyle.FULL, new Locale("es"));
            btn.setToolTipText(nombreDia + " " + d + " – "
                    + (laborable ? "Laborable" : "No laborable") + " (clic para cambiar)");

            btn.addActionListener(e -> {
                boolean actual = estadoDias.getOrDefault(fecha, true);
                estadoDias.put(fecha, !actual);
                refrescarCalendario();
            });

            panelDias.add(btn);
        }

        // Relleno final
        int total = offset + diasEnMes;
        int resto = total % 7;
        if (resto > 0) {
            for (int i = 0; i < 7 - resto; i++) panelDias.add(celdaVacia());
        }

        actualizarResumen();
        panelDias.revalidate();
        panelDias.repaint();
    }

    private JLabel celdaVacia() {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(new Color(245, 245, 245));
        return l;
    }

    private void marcarTodo(boolean laborable) {
        int diasEnMes = LocalDate.of(anioActual, mesActual, 1).lengthOfMonth();
        for (int d = 1; d <= diasEnMes; d++)
            estadoDias.put(LocalDate.of(anioActual, mesActual, d), laborable);
        refrescarCalendario();
    }

    private void aplicarDefectos() {
        int diasEnMes = LocalDate.of(anioActual, mesActual, 1).lengthOfMonth();
        for (int d = 1; d <= diasEnMes; d++) {
            LocalDate f = LocalDate.of(anioActual, mesActual, d);
            estadoDias.put(f, f.getDayOfWeek() != DayOfWeek.SUNDAY);
        }
        refrescarCalendario();
    }

    private void actualizarResumen() {
        long labs = estadoDias.values().stream().filter(v -> v).count();
        lblResumen.setText("Días laborables: " + labs);
    }

    // ── API pública ───────────────────────────────────────────────────────────
    public Map<LocalDate, Boolean> getEstadoDias() {
        return Collections.unmodifiableMap(estadoDias);
    }

    /** Restaura el estado desde persistencia. */
    public void setEstadoDias(Map<LocalDate, Boolean> dias) {
        if (dias != null && !dias.isEmpty()) {
            estadoDias.putAll(dias);
            // Navegar al mes del primer día guardado
            LocalDate primero = dias.keySet().stream()
                    .min(LocalDate::compareTo).orElse(null);
            if (primero != null) {
                anioActual = primero.getYear();
                mesActual  = primero.getMonthValue();
                spnAnio.setValue(anioActual);
                cmbMes.setSelectedIndex(mesActual - 1);
            }
        }
        refrescarCalendario();
    }

    public int getDiasLaborables() {
        return (int) estadoDias.values().stream().filter(v -> v).count();
    }

    /** Genera jornadas laborales para el estado actual del calendario. */
    public List<JornadaLaboral> generarJornadas(int horaInicio, int horaAlmFin,
                                                  int horaRean, int horaFin,
                                                  boolean partida) {
        List<JornadaLaboral> lista = new ArrayList<>();
        List<LocalDate> fechas = new ArrayList<>(estadoDias.keySet());
        Collections.sort(fechas);
        int numDia = 1;
        for (LocalDate f : fechas) {
            boolean lab = estadoDias.getOrDefault(f, false);
            JornadaLaboral j;
            if (lab) {
                j = partida
                        ? JornadaLaboral.partida(numDia, horaInicio, horaAlmFin, horaRean, horaFin)
                        : JornadaLaboral.continua(numDia, horaInicio, horaFin);
            } else {
                j = new JornadaLaboral(numDia, false);
            }
            lista.add(j);
            numDia++;
        }
        return lista;
    }

    private JPanel cuadro(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        JLabel c = new JLabel("  ");
        c.setOpaque(true); c.setBackground(color);
        c.setBorder(BorderFactory.createLineBorder(color.darker()));
        p.add(c); p.add(new JLabel(texto));
        return p;
    }
}