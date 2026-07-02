package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.model.Socio;
import com.supermercado.domain.cooperativa.model.TipoCaja;

import java.util.List;
import java.util.Random;

public class GeneradorSociosService {
    private final Random random;
    private int contadorSocios;

    public GeneradorSociosService() {
        this.random = new Random();
        this.contadorSocios = 0;
    }

    public Socio generarSocio(List<ServicioFinanciero> servicios, List<TipoCaja> tiposCaja) {
        // Seleccionar servicio según probabilidades
        ServicioFinanciero servicio = seleccionarServicio(servicios);
        
        // Crear socio
        String id = "SOC-" + String.format("%03d", ++contadorSocios);
        Socio socio = new Socio(id, servicio);
        
        // Asignar monto aleatorio dentro del rango del servicio
        double monto = servicio.getMontoMinimo() + 
                       (servicio.getMontoMaximo() - servicio.getMontoMinimo()) * random.nextDouble();
        socio.setMonto(Math.round(monto * 100.0) / 100.0); // Redondear a 2 decimales
        
        // Asignar duración estimada (en minutos simulados)
        int duracion = servicio.getDuracionMinima() + 
                       random.nextInt(servicio.getDuracionMaxima() - servicio.getDuracionMinima() + 1);
        socio.setDuracionEstimada(duracion);
        
        // Asignar tiempo de llegada (se establecerá externamente)
        socio.setTiempoLlegada(System.currentTimeMillis());
        
        // Determinar si es preferente (10% de probabilidad)
        socio.setEsPreferente(random.nextDouble() < 0.10);
        
        // Asignar prioridad (1=alta, 2=normal, 3=baja)
        if (socio.isEsPreferente()) {
            socio.setPrioridad(1);
        } else {
            socio.setPrioridad(2 + random.nextInt(2)); // 2 o 3
        }
        
        // Generar ficha con prefijo según tipo de caja permitido
        String prefijo = seleccionarPrefijoTipoCaja(servicio, tiposCaja);
        String numeroFicha = String.format("%03d", contadorSocios);
        socio.setFicha(prefijo + numeroFicha);
        
        return socio;
    }

    private ServicioFinanciero seleccionarServicio(List<ServicioFinanciero> servicios) {
        // Filtrar servicios activos
        List<ServicioFinanciero> activos = servicios.stream()
                .filter(ServicioFinanciero::isActivo)
                .toList();
        
        if (activos.isEmpty()) {
            throw new IllegalStateException("No hay servicios financieros activos");
        }
        
        // Calcular probabilidad acumulada
        double totalProbabilidad = activos.stream().mapToDouble(ServicioFinanciero::getProbabilidad).sum();
        double valor = random.nextDouble() * totalProbabilidad;
        double acumulado = 0.0;
        
        for (ServicioFinanciero s : activos) {
            acumulado += s.getProbabilidad();
            if (valor <= acumulado) {
                return s;
            }
        }
        return activos.get(activos.size() - 1);
    }

    private String seleccionarPrefijoTipoCaja(ServicioFinanciero servicio, List<TipoCaja> tiposCaja) {
        // Filtrar tipos de caja permitidos para este servicio
        List<TipoCaja> permitidos = tiposCaja.stream()
                .filter(TipoCaja::isActivo)
                .filter(t -> servicio.getTiposCajaPermitidos().contains(t.getId()))
                .toList();
        
        if (permitidos.isEmpty()) {
            // Si no hay tipos específicos, usar "GEN" (General)
            return "GEN";
        }
        
        // Seleccionar aleatoriamente entre los permitidos
        TipoCaja seleccionado = permitidos.get(random.nextInt(permitidos.size()));
        return seleccionado.getPrefijoFicha();
    }

    public void reiniciarContador() {
        this.contadorSocios = 0;
    }
}