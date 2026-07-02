package com.supermercado.application.cooperativa.usecase;

import com.supermercado.application.cooperativa.dto.ConfiguracionCooperativaDTO;
import com.supermercado.application.cooperativa.dto.ServicioDTO;
import com.supermercado.application.cooperativa.dto.TipoCajaDTO;
import com.supermercado.domain.cooperativa.model.Caja;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.model.TipoCaja;
import com.supermercado.domain.cooperativa.service.SimuladorCooperativaService;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;

import java.util.ArrayList;
import java.util.List;

public class IniciarSimulacionCooperativaUseCase {
    private final SimuladorCooperativaService simulador;
    private final EventBusAdapter eventBus;

    public IniciarSimulacionCooperativaUseCase(SimuladorCooperativaService simulador, EventBusAdapter eventBus) {
        this.simulador = simulador;
        this.eventBus = eventBus;
    }

    public void execute(ConfiguracionCooperativaDTO config) {
        if (simulador.isEjecutando()) {
            throw new IllegalStateException("La simulación ya está en ejecución");
        }

        // Convertir DTOs a entidades
        List<ServicioFinanciero> servicios = convertirServicios(config.getServicios());
        List<TipoCaja> tiposCaja = convertirTiposCaja(config.getTiposCaja());
        List<Caja> cajas = crearCajas(config.getNumCajas(), tiposCaja);

        // Iniciar simulación
        simulador.iniciarSimulacion(config.getDuracionPrincipal());
    }

    private List<ServicioFinanciero> convertirServicios(List<ServicioDTO> serviciosDTO) {
        List<ServicioFinanciero> servicios = new ArrayList<>();
        if (serviciosDTO == null) return servicios;
        
        for (ServicioDTO dto : serviciosDTO) {
            ServicioFinanciero servicio = new ServicioFinanciero(dto.getId(), dto.getNombre());
            servicio.setDuracionMinima(dto.getDuracionMinima());
            servicio.setDuracionMaxima(dto.getDuracionMaxima());
            servicio.setMontoMinimo(dto.getMontoMinimo());
            servicio.setMontoMaximo(dto.getMontoMaximo());
            servicio.setTasaInteres(dto.getTasaInteres());
            servicio.setProbabilidad(dto.getProbabilidad());
            servicio.setTiposCajaPermitidos(dto.getTiposCajaPermitidos());
            servicio.setActivo(dto.isActivo());
            servicios.add(servicio);
        }
        return servicios;
    }

    private List<TipoCaja> convertirTiposCaja(List<TipoCajaDTO> tiposCajaDTO) {
        List<TipoCaja> tiposCaja = new ArrayList<>();
        if (tiposCajaDTO == null) return tiposCaja;
        
        for (TipoCajaDTO dto : tiposCajaDTO) {
            TipoCaja tipo = new TipoCaja(dto.getId(), dto.getNombre(), dto.getPrefijoFicha());
            tipo.setFactorVelocidad(dto.getFactorVelocidad());
            tipo.setPrioridad(dto.getPrioridad());
            tipo.setServiciosEspecializados(dto.getServiciosEspecializados());
            tipo.setActivo(dto.isActivo());
            tiposCaja.add(tipo);
        }
        return tiposCaja;
    }

    private List<Caja> crearCajas(int numCajas, List<TipoCaja> tiposCaja) {
        List<Caja> cajas = new ArrayList<>();
        if (tiposCaja == null || tiposCaja.isEmpty()) {
            // Si no hay tipos de caja, usar uno por defecto
            TipoCaja defaultTipo = new TipoCaja("GEN", "General", "GEN");
            for (int i = 1; i <= numCajas; i++) {
                cajas.add(new Caja("C-" + String.format("%03d", i), defaultTipo));
            }
        } else {
            // Distribuir tipos de caja de forma equitativa (alternando)
            int tipoIndex = 0;
            for (int i = 1; i <= numCajas; i++) {
                TipoCaja tipo = tiposCaja.get(tipoIndex % tiposCaja.size());
                cajas.add(new Caja("C-" + String.format("%03d", i), tipo));
                tipoIndex++;
            }
        }
        return cajas;
    }
}