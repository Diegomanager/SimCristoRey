package com.supermercado.presentation.cooperativa.view;

import com.supermercado.domain.cooperativa.model.JornadaLaboral;
import com.supermercado.domain.cooperativa.model.BloqueHorario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

public class PanelCalendario extends JPanel {

    private final JLabel lblMesAnio = new JLabel();
    private final JPanel panelDias = new JPanel(new GridLayout(0, 7, 2, 2));
    private final Map<LocalDate, Boolean> estadoDias = new LinkedHashMap<>();
    private YearMonth mesActual;

    public PanelCalendario() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Calendario - seleccione días laborables"));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAnt = new JButton("<");
        JButton btnSig = new JButton(">");
        btnAnt.addActionListener(e -> cambiarMes(-1));
        btnSig.addActionListener(e -> cambiarMes(1));
        barra.add(btnAnt);
        barra.add(lblMesAnio);
        barra.add(btnSig);
        add(barra, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelDias);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        mesActual = YearMonth.now();
        actualizar();
    }

    private void cambiarMes(int delta) {
        mesActual = mesActual.plusMonths(delta);
        actualizar();
    }

    private void actualizar() {
        panelDias.removeAll();
        lblMesAnio.setText(mesActual.getMonth().toString() + " " + mesActual.getYear());

        String[] diasSemana = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        for (String d : diasSemana) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
            panelDias.add(lbl);
        }

        LocalDate primero = mesActual.atDay(1);
        int offset = primero.getDayOfWeek().getValue() - 1; // 1=Lunes, 7=Domingo
        for (int i = 0; i < offset; i++) {
            panelDias.add(new JLabel(""));
        }

        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            JButton btn = new JButton(String.valueOf(dia));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            boolean activo = estadoDias.getOrDefault(fecha, false);
            btn.setBackground(activo ? new Color(100, 200, 100) : Color.WHITE);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    boolean nuevo = !estadoDias.getOrDefault(fecha, false);
                    estadoDias.put(fecha, nuevo);
                    btn.setBackground(nuevo ? new Color(100, 200, 100) : Color.WHITE);
                    btn.repaint();
                }
            });
            panelDias.add(btn);
        }

        panelDias.revalidate();
        panelDias.repaint();
    }

    // NUEVO: Limpia completamente el mapa y luego agrega los nuevos días
    public void setEstadoDias(Map<LocalDate, Boolean> nuevoEstado) {
        estadoDias.clear();
        if (nuevoEstado != null) {
            estadoDias.putAll(nuevoEstado);
        }
        actualizar();
    }

    public Map<LocalDate, Boolean> getEstadoDias() {
        return new LinkedHashMap<>(estadoDias);
    }

    public int getDiasLaborables() {
        return (int) estadoDias.values().stream().filter(Boolean::booleanValue).count();
    }

    public List<JornadaLaboral> generarJornadas(int horaInicio, int horaAlmuerzoFin,
                                                 int horaReanudacion, int horaFin,
                                                 boolean partida) {
        List<JornadaLaboral> lista = new ArrayList<>();
        int cont = 1;
        for (Map.Entry<LocalDate, Boolean> e : estadoDias.entrySet()) {
            boolean laborable = e.getValue();
            JornadaLaboral j = new JornadaLaboral(cont++, laborable);
            if (laborable) {
                j.setFechaReal(e.getKey());
                if (partida) {
                    j.agregarBloque(new BloqueHorario(horaInicio, horaAlmuerzoFin));
                    j.agregarBloque(new BloqueHorario(horaReanudacion, horaFin));
                } else {
                    j.agregarBloque(new BloqueHorario(horaInicio, horaFin));
                }
            }
            lista.add(j);
        }
        return lista;
    }
}