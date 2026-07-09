package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.ResumenDiario;
import com.supermercado.domain.cooperativa.model.Socio;

import java.util.*;

public class EstadisticasFinancierasService {

    private final EstadisticasFase principal = new EstadisticasFase("Principal");
    private final EstadisticasFase rezagados  = new EstadisticasFase("Rezagados");
    private boolean enFaseRezagados = false;
    private int     totalGeneradosDia = 0;

    private final List<ResumenDiario> resumenesDiarios = new ArrayList<>();

    private int    acumGenerados   = 0;
    private int    acumAtendPpal   = 0;
    private int    acumAtendRez    = 0;
    private double acumMonto       = 0.0;
    private double acumMontoPpal   = 0.0;   // NUEVO
    private double acumMontoRez    = 0.0;   // NUEVO
    private double acumSumaEspera  = 0.0;
    private double acumSumaAtencion= 0.0;
    private int    diasAcumulados  = 0;

    public void setFaseRezagados(boolean v) { enFaseRezagados = v; }
    public void setTotalGenerados(int v)    { totalGeneradosDia = v; }

    public void registrarAtencion(Socio socio, Caja caja) {
        if (enFaseRezagados) rezagados.registrar(socio, caja);
        else                 principal.registrar(socio, caja);
    }

    public void acumularDia(ResumenDiario r) {
        if (r == null || !r.isLaborable()) return;
        resumenesDiarios.add(r);
        acumGenerados  += r.getGenerados();
        acumAtendPpal  += r.getAtendidosPrincipal();
        acumAtendRez   += r.getAtendidosRezagados();
        acumMonto      += r.getMontoTotal();
        acumMontoPpal  += principal.getMontoTotal();
        acumMontoRez   += rezagados.getMontoTotal();
        int atend = r.getTotalAtendidos();
        if (atend > 0) {
            acumSumaEspera   += r.getPromedioEspera()   * atend;
            acumSumaAtencion += r.getPromedioAtencion() * atend;
        }
        diasAcumulados++;
    }

    public void registrarResumenDiario(ResumenDiario r) { acumularDia(r); }

    public void reiniciar() {
        principal.reiniciar(); rezagados.reiniciar();
        enFaseRezagados = false; totalGeneradosDia = 0;
    }

    public void reiniciarCompleto() {
        reiniciar();
        resumenesDiarios.clear();
        acumGenerados=0; acumAtendPpal=0; acumAtendRez=0;
        acumMonto=0.0; acumMontoPpal=0.0; acumMontoRez=0.0;
        acumSumaEspera=0.0; acumSumaAtencion=0.0; diasAcumulados=0;
    }

    public EstadisticasFase getPrincipal()   { return principal; }
    public EstadisticasFase getRezagados()   { return rezagados; }
    public int    getTotalAtendidos()        { return principal.getTotalAtendidos() + rezagados.getTotalAtendidos(); }
    public double getMontoTotal()            { return principal.getMontoTotal() + rezagados.getMontoTotal(); }
    public double getPromedioEspera() {
        int t = getTotalAtendidos(); if (t==0) return 0;
        return (principal.getPromedioEspera()*principal.getTotalAtendidos()
              + rezagados.getPromedioEspera()*rezagados.getTotalAtendidos()) / t;
    }
    public double getPromedioAtencion() {
        int t = getTotalAtendidos(); if (t==0) return 0;
        return (principal.getPromedioAtencion()*principal.getTotalAtendidos()
              + rezagados.getPromedioAtencion()*rezagados.getTotalAtendidos()) / t;
    }
    public String getCajeroEstrellaGlobal() {
        Map<String,Integer> combinado = new HashMap<>(principal.getAtendidosPorCaja());
        rezagados.getAtendidosPorCaja().forEach((k,v)->combinado.merge(k,v,Integer::sum));
        return combinado.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");
    }
    public double getEficienciaGlobal(long minutosSimulados) {
        return minutosSimulados==0 ? 0 : (double)getTotalAtendidos()/minutosSimulados;
    }

    public int    getAcumGenerados()         { return acumGenerados; }
    public int    getAcumAtendPpal()         { return acumAtendPpal; }
    public int    getAcumAtendRez()          { return acumAtendRez; }
    public int    getAcumTotalAtendidos()    { return acumAtendPpal + acumAtendRez; }
    public int    getAcumNoAtendidos()       { return Math.max(0, acumGenerados - getAcumTotalAtendidos()); }
    public double getAcumMonto()             { return acumMonto; }
    public double getAcumMontoPpal()         { return acumMontoPpal; }
    public double getAcumMontoRez()          { return acumMontoRez; }
    public int    getDiasAcumulados()        { return diasAcumulados; }
    public double getAcumPromedioEspera()    { int t = getAcumTotalAtendidos(); return t==0?0:acumSumaEspera/t; }
    public double getAcumPromedioAtencion()  { int t = getAcumTotalAtendidos(); return t==0?0:acumSumaAtencion/t; }
    public List<ResumenDiario> getResumenesDiarios() { return Collections.unmodifiableList(resumenesDiarios); }
}