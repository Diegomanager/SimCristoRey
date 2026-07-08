package com.supermercado.domain.cooperativa.config;

import com.supermercado.domain.cooperativa.model.JornadaLaboral;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ConfiguracionCooperativa {
    private static final String ARCHIVO = "simcristorey.properties";
    private final Properties props = new Properties();

    private double horasJornadaDia  = 8.0;
    private int    diasASimular     = 1;
    private long   msPorMinuto      = 62L;
    private int    maxSociosDia     = 200;
    private double intervaloMinutos = 1.0;

    private int     horaInicio      = 510;
    private int     horaFin         = 990;
    private boolean jornadaPartida  = true;
    private int     horaAlmuerzoFin = 720;
    private int     horaReanudacion = 870;

    private int cajasGenerales  = 7;
    private int cajasPlataforma = 2;
    private int cajasMM         = 1;

    private Map<String, Double> probPorCodigo = new LinkedHashMap<>();
    private Map<LocalDate, Boolean> diasLaborables = new LinkedHashMap<>();

    public ConfiguracionCooperativa() {
        inicializarProbabilidadesPorDefecto();
        cargar();
    }

    private void inicializarProbabilidadesPorDefecto() {
        probPorCodigo.put("C",  0.20);
        probPorCodigo.put("A",  0.08);
        probPorCodigo.put("S",  0.25);
        probPorCodigo.put("F",  0.05);
        probPorCodigo.put("P",  0.10);
        probPorCodigo.put("R",  0.15);
        probPorCodigo.put("PC", 0.04);
        probPorCodigo.put("PS", 0.05);
        probPorCodigo.put("PA", 0.04);
        probPorCodigo.put("PP", 0.04);
    }

    public double getProbCodigo(String codigo) { return probPorCodigo.getOrDefault(codigo, 0.0); }
    public void setProbCodigo(String codigo, double prob) {
        if (codigo != null && !codigo.isBlank())
            probPorCodigo.put(codigo, Math.max(0.0, prob));
    }
    public void eliminarProbCodigo(String codigo) { probPorCodigo.remove(codigo); }
    public void agregarCodigoNuevo(String codigo) { probPorCodigo.putIfAbsent(codigo, 0.01); }
    public Map<String, Double> getProbPorCodigo() { return Collections.unmodifiableMap(probPorCodigo); }

    public void normalizarProbabilidades() {
        double suma = probPorCodigo.values().stream().mapToDouble(Double::doubleValue).sum();
        if (suma <= 0) return;
        probPorCodigo.replaceAll((k, v) -> v / suma);
    }

    public int getDuracionDiaMinutos()  { return (int)(horasJornadaDia * 60); }
    public int getDuracionTotalMinutos(){ return getDuracionDiaMinutos() * Math.max(1, diasASimular); }

    public void cargar() {
        try (InputStream in = new FileInputStream(ARCHIVO)) {
            props.load(in);
            horasJornadaDia  = pd("horas.jornada.dia",  8.0);
            diasASimular     = pi("dias.simular",        1);
            msPorMinuto      = pl("escala.ms",           62L);
            maxSociosDia     = pi("max.socios.dia",      200);
            intervaloMinutos = pd("intervalo.minutos",   1.0);
            horaInicio       = pi("hora.inicio",         510);
            horaFin          = pi("hora.fin",            990);
            jornadaPartida   = pb("jornada.partida",     true);
            horaAlmuerzoFin  = pi("hora.almuerzo.fin",   720);
            horaReanudacion  = pi("hora.reanudacion",    870);
            cajasGenerales   = pi("cajas.generales",     7);
            cajasPlataforma  = pi("cajas.plataforma",    2);
            cajasMM          = pi("cajas.mm",            1);
            diasLaborables   = deserializarDias(props.getProperty("dias.laborables", ""));
            String codigosStr = props.getProperty("prob.codigos", "");
            if (!codigosStr.isBlank()) {
                Map<String, Double> cargado = new LinkedHashMap<>();
                for (String par : codigosStr.split(",")) {
                    String[] kv = par.trim().split("=");
                    if (kv.length == 2) try { cargado.put(kv[0].trim(), Double.parseDouble(kv[1].trim())); } catch(Exception ignored){}
                }
                if (!cargado.isEmpty()) probPorCodigo = cargado;
            }
        } catch (IOException e) { guardar(); }
    }

    public void guardar() {
        set("horas.jornada.dia",  horasJornadaDia);
        set("dias.simular",       diasASimular);
        set("escala.ms",          msPorMinuto);
        set("max.socios.dia",     maxSociosDia);
        set("intervalo.minutos",  intervaloMinutos);
        set("hora.inicio",        horaInicio);
        set("hora.fin",           horaFin);
        set("jornada.partida",    jornadaPartida);
        set("hora.almuerzo.fin",  horaAlmuerzoFin);
        set("hora.reanudacion",   horaReanudacion);
        set("cajas.generales",    cajasGenerales);
        set("cajas.plataforma",   cajasPlataforma);
        set("cajas.mm",           cajasMM);
        props.setProperty("dias.laborables", serializarDias(diasLaborables));
        StringBuilder sb = new StringBuilder();
        probPorCodigo.forEach((k,v) -> { if(sb.length()>0) sb.append(","); sb.append(k).append("=").append(v); });
        props.setProperty("prob.codigos", sb.toString());

        try (OutputStream o = new FileOutputStream(ARCHIVO)) {
            props.store(o, "SimCristoRey Config");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<JornadaLaboral> generarJornadas() {
        List<JornadaLaboral> lista = new ArrayList<>();
        if (!diasLaborables.isEmpty()) {
            List<LocalDate> fechas = new ArrayList<>(diasLaborables.keySet());
            Collections.sort(fechas);
            int numDia = 1;
            for (LocalDate f : fechas) {
                boolean lab = diasLaborables.getOrDefault(f, false);
                JornadaLaboral j = construirJornada(numDia++, lab);
                j.setFechaReal(f);
                lista.add(j);
            }
        } else {
            for (int d = 1; d <= Math.max(1, diasASimular); d++)
                lista.add(construirJornada(d, true));
        }
        return lista;
    }

    private JornadaLaboral construirJornada(int dia, boolean laborable) {
        if (!laborable) return new JornadaLaboral(dia, false);
        if (jornadaPartida)
            return JornadaLaboral.partida(dia, horaInicio, horaAlmuerzoFin, horaReanudacion, horaFin);
        return JornadaLaboral.continua(dia, horaInicio, horaFin);
    }

    private String serializarDias(Map<LocalDate, Boolean> m) {
        if (m == null || m.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        m.forEach((f,v) -> { if(sb.length()>0) sb.append(","); sb.append(f).append("=").append(v); });
        return sb.toString();
    }

    private Map<LocalDate, Boolean> deserializarDias(String s) {
        Map<LocalDate, Boolean> m = new LinkedHashMap<>();
        if (s==null||s.isBlank()) return m;
        for (String par : s.split(",")) {
            String[] kv=par.trim().split("=");
            if(kv.length==2) try{m.put(LocalDate.parse(kv[0].trim()),Boolean.parseBoolean(kv[1].trim()));}catch(Exception ignored){}
        }
        return m;
    }

    private void set(String k,Object v){props.setProperty(k,String.valueOf(v));}
    private int     pi(String k,int d)    {try{return Integer.parseInt(props.getProperty(k,String.valueOf(d)));}catch(Exception e){return d;}}
    private long    pl(String k,long d)   {try{return Long.parseLong(props.getProperty(k,String.valueOf(d)));}catch(Exception e){return d;}}
    private double  pd(String k,double d) {try{return Double.parseDouble(props.getProperty(k,String.valueOf(d)));}catch(Exception e){return d;}}
    private boolean pb(String k,boolean d){try{return Boolean.parseBoolean(props.getProperty(k,String.valueOf(d)));}catch(Exception e){return d;}}

    public double  getHorasJornadaDia()           { return horasJornadaDia; }
    public void    setHorasJornadaDia(double v)   { horasJornadaDia = v; }
    public int     getDiasASimular()              { return diasASimular; }
    public void    setDiasASimular(int v)         { diasASimular = Math.max(1, Math.min(31, v)); }
    public long    getMsPorMinuto()               { return msPorMinuto; }
    public void    setMsPorMinuto(long v)         { msPorMinuto = v; }
    public int     getMaxSociosDia()              { return maxSociosDia; }
    public void    setMaxSociosDia(int v)         { maxSociosDia = v; }
    public double  getIntervaloMinutos()          { return intervaloMinutos; }
    public void    setIntervaloMinutos(double v)  { intervaloMinutos = v; }
    public int     getHoraInicio()                { return horaInicio; }
    public void    setHoraInicio(int v)           { horaInicio = v; }
    public int     getHoraFin()                   { return horaFin; }
    public void    setHoraFin(int v)              { horaFin = v; }
    public boolean isJornadaPartida()             { return jornadaPartida; }
    public void    setJornadaPartida(boolean v)   { jornadaPartida = v; }
    public int     getHoraAlmuerzoFin()           { return horaAlmuerzoFin; }
    public void    setHoraAlmuerzoFin(int v)      { horaAlmuerzoFin = v; }
    public int     getHoraReanudacion()           { return horaReanudacion; }
    public void    setHoraReanudacion(int v)      { horaReanudacion = v; }
    public int     getCajasGenerales()            { return cajasGenerales; }
    public void    setCajasGenerales(int v)       { cajasGenerales = v; }
    public int     getCajasPlataforma()           { return cajasPlataforma; }
    public void    setCajasPlataforma(int v)      { cajasPlataforma = v; }
    public int     getCajasMM()                   { return cajasMM; }
    public void    setCajasMM(int v)              { cajasMM = v; }
    public Map<LocalDate, Boolean> getDiasLaborables()          { return diasLaborables; }
    public void setDiasLaborables(Map<LocalDate, Boolean> dias) { this.diasLaborables = dias != null ? dias : new LinkedHashMap<>(); }
    public double[] getProbabilidadesArray() {
        String[] orden = {"C","S","P","A","F","R","PC","PS","PA","PP"};
        double[] arr = new double[orden.length];
        for (int i=0;i<orden.length;i++) arr[i]=probPorCodigo.getOrDefault(orden[i],0.0);
        return arr;
    }
}