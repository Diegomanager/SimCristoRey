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
        if (simulador.isCorriendo()) {
            throw new IllegalStateException("La simulacion ya esta en ejecucion");
        }

        // Convertir DTOs a entidades
        List<ServicioFinanciero> servicios = convertirServicios(config.getServicios());
        List<TipoCaja> tiposCaja = convertirTiposCaja(config.getTiposCaja());
        List<Caja> cajas = crearCajas(config.getNumCajas(), tiposCaja);

        // Configurar el simulador
        int duracionMinutos = config.getDuracionPrincipal();
        long msPorMinuto = SimuladorCooperativaService.calcularEscala(duracionMinutos, 30); // 30 seg reales
        int maxSocios = config.getCapacidadMaximaSocios();
        double intervaloLlegada = 1.0;

        simulador.configurar(duracionMinutos, msPorMinuto, maxSocios, intervaloLlegada, servicios, cajas);

        // Iniciar
        simulador.iniciar();
    }

    private List<ServicioFinanciero> convertirServicios(List<ServicioDTO> serviciosDTO) {
        List<ServicioFinanciero> servicios = new ArrayList<>();
        if (serviciosDTO == null) return servicios;

        for (ServicioDTO dto : serviciosDTO) {
            ServicioFinanciero s = new ServicioFinanciero(dto.getId(), dto.getNombre());
            s.setDuracionMinima(dto.getDuracionMinima());
            s.setDuracionMaxima(dto.getDuracionMaxima());
            s.setMontoMinimo(dto.getMontoMinimo());
            s.setMontoMaximo(dto.getMontoMaximo());
            s.setTasaInteres(dto.getTasaInteres());
            s.setProbabilidad(dto.getProbabilidad());
            s.setTiposCajaPermitidos(dto.getTiposCajaPermitidos());
            s.setActivo(dto.isActivo());
            // Asignar tipo requerido (primero de la lista de permitidos)
            if (dto.getTiposCajaPermitidos() != null && !dto.getTiposCajaPermitidos().isEmpty()) {
                s.setTipoCajaRequerido(dto.getTiposCajaPermitidos().iterator().next());
            } else {
                s.setTipoCajaRequerido("GENERAL");
            }
            servicios.add(s);
        }
        return servicios;
    }

    private List<TipoCaja> convertirTiposCaja(List<TipoCajaDTO> tiposCajaDTO) {
        List<TipoCaja> tipos = new ArrayList<>();
        if (tiposCajaDTO == null) return tipos;

        for (TipoCajaDTO dto : tiposCajaDTO) {
            TipoCaja t = new TipoCaja(dto.getId(), dto.getNombre(), dto.getPrefijoFicha());
            t.setFactorVelocidad(dto.getFactorVelocidad());
            t.setPrioridad(dto.getPrioridad());
            t.setServiciosEspecializados(dto.getServiciosEspecializados());
            t.setActivo(dto.isActivo());
            tipos.add(t);
        }
        return tipos;
    }

    private List<Caja> crearCajas(int numCajas, List<TipoCaja> tiposCaja) {
        List<Caja> cajas = new ArrayList<>();
        if (tiposCaja == null || tiposCaja.isEmpty()) {
            TipoCaja defaultTipo = new TipoCaja("GENERAL", "General", "GEN");
            for (int i = 1; i <= numCajas; i++) {
                cajas.add(new Caja("C-" + String.format("%03d", i), defaultTipo));
            }
        } else {
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