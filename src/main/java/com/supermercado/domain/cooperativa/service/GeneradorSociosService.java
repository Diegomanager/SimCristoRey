package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.model.Socio;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneradorSociosService {

    private final AtomicInteger contadorSocios   = new AtomicInteger(0);
    private final AtomicInteger contadorFichas   = new AtomicInteger(0);
    private final Random        random           = new Random();

    private static final Map<String, String> PREFIJOS = Map.of(
            "GENERAL",  "GEN",
            "CREDITOS", "CRE",
            "RECLAMO",  "REC",
            "AHORRO",   "AHO",
            "MM",       "MM"
    );

    private static final double[] LIMITES = { 0.65, 0.85, 0.90, 0.95, 1.00 };
    private static final String[] TIPOS   = { "GENERAL", "CREDITOS", "RECLAMO", "AHORRO", "MM" };

    private List<ServicioFinanciero> serviciosDisponibles = new ArrayList<>();

    public void setServicios(List<ServicioFinanciero> servicios) {
        this.serviciosDisponibles = servicios != null ? servicios : new ArrayList<>();
    }

    public Socio generarSocio(long tiempoLlegada) {
        int numSocio = contadorSocios.incrementAndGet();

        String tipoCajaId = sortearTipoCaja();
        ServicioFinanciero servicio = elegirServicio(tipoCajaId);

        int numFicha   = contadorFichas.incrementAndGet();
        String prefijo = PREFIJOS.getOrDefault(tipoCajaId, "GEN");
        String ficha   = String.format("%s%03d", prefijo, numFicha);

        Socio socio = new Socio();
        socio.setId("SOC-" + String.format("%04d", numSocio));
        socio.setFicha(ficha);
        socio.setServicio(servicio);
        socio.setTiempoLlegada(tiempoLlegada);
        socio.setAtendida(false);

        if (servicio != null) {
            double monto = servicio.getMontoMinimo()
                    + random.nextDouble() * (servicio.getMontoMaximo() - servicio.getMontoMinimo());
            socio.setMonto(monto);

            int duracion = servicio.getDuracionMinima()
                    + random.nextInt(Math.max(1,
                        servicio.getDuracionMaxima() - servicio.getDuracionMinima() + 1));
            socio.setDuracionEstimada(duracion);
        } else {
            socio.setMonto(500 + random.nextDouble() * 4500);
            socio.setDuracionEstimada(5 + random.nextInt(15));
        }

        boolean preferente = random.nextDouble() < 0.10;
        socio.setEsPreferente(preferente);
        socio.setPrioridad(preferente ? 1 : 3);

        return socio;
    }

    private String sortearTipoCaja() {
        double r = random.nextDouble();
        for (int i = 0; i < LIMITES.length; i++) {
            if (r < LIMITES[i]) return TIPOS[i];
        }
        return "GENERAL";
    }

    private ServicioFinanciero elegirServicio(String tipoCajaId) {
        if (serviciosDisponibles.isEmpty()) return null;
        List<ServicioFinanciero> compatibles = new ArrayList<>();
        for (ServicioFinanciero s : serviciosDisponibles) {
            if (s.isActivo() && tipoCajaId.equals(s.getTipoCajaRequerido())) {
                compatibles.add(s);
            }
        }
        if (compatibles.isEmpty()) {
            List<ServicioFinanciero> activos = serviciosDisponibles.stream()
                    .filter(ServicioFinanciero::isActivo).toList();
            if (activos.isEmpty()) return null;
            return activos.get(random.nextInt(activos.size()));
        }
        return compatibles.get(random.nextInt(compatibles.size()));
    }

    public void reiniciar() {
        contadorSocios.set(0);
        contadorFichas.set(0);
    }

    public int getTotalGenerados() { return contadorSocios.get(); }
}