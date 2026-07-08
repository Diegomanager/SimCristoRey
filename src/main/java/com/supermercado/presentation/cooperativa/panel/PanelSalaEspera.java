package com.supermercado.presentation.cooperativa.panel;

import com.supermercado.domain.cooperativa.model.SalaEspera;
import com.supermercado.domain.cooperativa.model.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelSalaEspera extends JPanel {
    private final DefaultTableModel modelo;
    private final JLabel lblTotal = new JLabel("En espera: 0");
    private final JLabel lblPref  = new JLabel("Preferentes: 0");
    private static final Color C_PREFERENTE = new Color(255, 240, 180);

    public PanelSalaEspera() {
        setLayout(new BorderLayout(4,4));
        setBorder(BorderFactory.createTitledBorder("🪑 Sala de Espera (⭐ = preferente)"));
        String[] cols = {"#","Ficha","Destino","Servicio(s)","Monto (Bs)","⭐"};
        modelo = new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setMaxWidth(35);
        tabla.getColumnModel().getColumn(1).setMaxWidth(75);
        tabla.getColumnModel().getColumn(2).setMaxWidth(80);
        tabla.getColumnModel().getColumn(5).setMaxWidth(25);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                String pref = (String)modelo.getValueAt(row,5);
                if("⭐".equals(pref)){
                    setBackground(sel?new Color(240,210,100):C_PREFERENTE);
                } else {
                    setBackground(sel?new Color(173,216,230):(row%2==0?Color.WHITE:new Color(245,248,255)));
                }
                setForeground(Color.BLACK);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        lblTotal.setFont(new Font("SansSerif",Font.BOLD,12));
        lblPref.setFont(new Font("SansSerif",Font.BOLD,12));
        lblPref.setForeground(new Color(160,100,0));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,10,3));
        top.add(lblTotal); top.add(lblPref);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void actualizar(SalaEspera sala) {
        if (sala==null) return;
        SwingUtilities.invokeLater(()->{
            List<Socio> socios = sala.getSocios();
            modelo.setRowCount(0);
            int pos=1;
            for (Socio s : socios) {
                if (s==null) continue;
                String dest = s.getTipoCajaDestino()!=null?s.getTipoCajaDestino():"GEN";
                String svc  = s.getDescripcionServicios();
                modelo.addRow(new Object[]{pos++,s.getFicha(),dest,svc,
                        String.format("%.2f",s.getMonto()),s.isEsPreferente()?"⭐":""});
            }
            lblTotal.setText("En espera: "+sala.getTotalEsperando());
            lblPref.setText("Preferentes: "+sala.getTotalPreferentesEsperando());
        });
    }

    public void limpiar() {
        SwingUtilities.invokeLater(()->{
            modelo.setRowCount(0);
            lblTotal.setText("En espera: 0");
            lblPref.setText("Preferentes: 0");
        });
    }
}