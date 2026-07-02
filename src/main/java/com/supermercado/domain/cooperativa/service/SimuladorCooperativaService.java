package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.event.*;
import com.supermercado.domain.cooperativa.model.*;
import com.supermercado.infrastructure.adapter.event.EventBusAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimuladorCooperativaService {
    private final SalaEspera salaEspera;
    private final List<Caja> cajas;
    private final List<ServicioFinanciero> servicios;
    private final List<TipoCaja> tiposCaja;
    private final GeneradorSociosService generadorSocios;
    private final AsignadorCajasService asignadorCajas;
    private final EstadisticasFinancierasService estadisticasService;
    private final EventBusAdapter eventBus;

    private ScheduledExecutorService executor;
    private AtomicBoolean ejecutando;
    private AtomicBoolean pausado;
    private AtomicBoolean detenido;
    private AtomicBoolean faseRezagados;

    private int duracionPrincipal; // en minutos simulados
    private int sociosGenerados;
    private int sociosAtendidos;
    private int sociosRezagados;
    private long tiempoInicioSimulacion;
    private long tiempoInicioFaseRezagados;

    public SimuladorCooperativaService(
            List<ServicioFinanciero> servicios,
            List<TipoCaja> tiposCaja,
            List<Caja> cajas,
            EventBusAdapter eventBus) {
        this.salaEspera = new SalaEspera();
        this.servicios = servicios;
        this.tiposCaja = tiposCaja;
        this.cajas = cajas;
        this.eventBus = eventBus;
        this.generadorSocios = new GeneradorSociosService();
        this.asignadorCajas = new AsignadorCajasService();
        this.estadisticasService = new EstadisticasFinancierasService();
        this.ejecutando = new AtomicBoolean(false);
        this.pausado = new AtomicBoolean(false);
        this.detenido = new AtomicBoolean(false);
        this.faseRezagados = new AtomicBoolean(false);
    }

    public void iniciarSimulacion(int duracionPrincipal) {
        if (ejecutando.get()) {
            return;
        }
        
        // Reiniciar estado
        this.duracionPrincipal = duracionPrincipal;
        this.sociosGenerados = 0;
        this.sociosAtendidos = 0;
        this.sociosRezagados = 0;
        this.tiempoInicioSimulacion = System.currentTimeMillis();
        this.faseRezagados.set(false);
        this.detenido.set(false);
        this.pausado.set(false);
        this.ejecutando.set(true);
        
        // Reiniciar sala de espera y cajas
        salaEspera.reiniciar();
        cajas.forEach(Caja::reiniciar);
        generadorSocios.reiniciarContador();
        
        // Publicar evento de inicio
        eventBus.publish(new SimulacionCooperativaIniciadaEvent());
        
        // Iniciar ejecución en hilo separado
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::tickSimulacion, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void tickSimulacion() {
        if (detenido.get()) {
            return;
        }
        
        if (pausado.get()) {
            return;
        }
        
        // Verificar si terminó la fase principal
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicioSimulacion;
        long minutosTranscurridos = tiempoTranscurrido / 1000 / 60; // simplificado para pruebas
        
        if (!faseRezagados.get() && minutosTranscurridos >= duracionPrincipal) {
            iniciarFaseRezagados();
            return;
        }
        
        if (faseRezagados.get()) {
            tickFaseRezagados();
        } else {
            tickFasePrincipal();
        }
    }

    private void tickFasePrincipal() {
        // Generar socios (simplificado: 1 por tick)
        Socio nuevoSocio = generadorSocios.generarSocio(servicios, tiposCaja);
        salaEspera.agregarSocio(nuevoSocio);
        sociosGenerados++;
        eventBus.publish(new SocioGeneradoEvent(nuevoSocio));
        
        // Asignar socios a cajas libres
        int asignados = asignadorCajas.asignarTodosPosibles(salaEspera, cajas);
        if (asignados > 0) {
            // Publicar eventos de asignación (los detalles se manejan internamente)
        }
        
        // Procesar finalización de atenciones
        for (Caja caja : cajas) {
            if (caja.getEstado() == EstadoCaja.OCUPADA) {
                // Simular que la atención toma tiempo (simplificado: finaliza después de duracionEstimada ticks)
                // En una implementación real, se usaría un timer
                // Por ahora, asumimos que finaliza después de la duración estimada
                // Para simplificar, finalizamos inmediatamente en el próximo tick
                caja.finalizarAtencion();
                sociosAtendidos++;
                eventBus.publish(new SocioAtendidoEvent(caja.getSocioActual(), caja));
            }
        }
    }

    private void tickFaseRezagados() {
        // No generar nuevos socios, solo atender rezagados
        if (salaEspera.isEmpty()) {
            // Verificar si todas las cajas están libres
            boolean todasLibres = cajas.stream().allMatch(c -> c.getEstado() == EstadoCaja.LIBRE);
            if (todasLibres) {
                finalizarSimulacion();
                return;
            }
        }
        
        // Asignar socios a cajas libres
        asignadorCajas.asignarTodosPosibles(salaEspera, cajas);
        
        // Procesar finalización de atenciones
        for (Caja caja : cajas) {
            if (caja.getEstado() == EstadoCaja.OCUPADA) {
                caja.finalizarAtencion();
                sociosAtendidos++;
                eventBus.publish(new SocioAtendidoEvent(caja.getSocioActual(), caja));
            }
        }
    }

    private void iniciarFaseRezagados() {
        if (faseRezagados.get()) {
            return;
        }
        
        sociosRezagados = salaEspera.getTotalEsperando();
        tiempoInicioFaseRezagados = System.currentTimeMillis();
        faseRezagados.set(true);
        
        eventBus.publish(new FaseRezagadosIniciadaEvent(sociosRezagados));
    }

    private void finalizarSimulacion() {
        if (!ejecutando.get()) {
            return;
        }
        
        detenido.set(true);
        ejecutando.set(false);
        executor.shutdown();
        
        eventBus.publish(new SimulacionCooperativaFinalizadaEvent());
    }

    public void pausar() {
        pausado.set(true);
        eventBus.publish(new SimulacionCooperativaPausadaEvent());
    }

    public void reanudar() {
        pausado.set(false);
        eventBus.publish(new SimulacionCooperativaReanudadaEvent());
    }

    public void detener() {
        detenido.set(true);
        ejecutando.set(false);
        if (executor != null) {
            executor.shutdownNow();
        }
        eventBus.publish(new SimulacionCooperativaDetenidaEvent());
    }

    public boolean isEjecutando() {
        return ejecutando.get();
    }

    public boolean isPausado() {
        return pausado.get();
    }

    public boolean isDetenido() {
        return detenido.get();
    }

    public boolean isFaseRezagados() {
        return faseRezagados.get();
    }

    public int getSociosGenerados() {
        return sociosGenerados;
    }

    public int getSociosAtendidos() {
        return sociosAtendidos;
    }

    public int getSociosRezagados() {
        return sociosRezagados;
    }

    public int getSociosEnEspera() {
        return salaEspera.getTotalEsperando();
    }

    public List<Caja> getCajas() {
        return new ArrayList<>(cajas);
    }
}