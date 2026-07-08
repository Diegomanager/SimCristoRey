package com.supermercado.domain.cooperativa.service;

import com.supermercado.domain.cooperativa.config.ConfiguracionCooperativa;
import com.supermercado.domain.cooperativa.model.ConfiguracionMultiServicio;
import com.supermercado.domain.cooperativa.model.ServicioFinanciero;
import com.supermercado.domain.cooperativa.model.Socio;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GeneradorSociosService {

    private static final Map<String, String> NOMBRES = new LinkedHashMap<>();
    static {
        NOMBRES.put("C",  "Socios Ahorro/Crédito");
        NOMBRES.put("A",  "Socios Semapa (Agua)");
        NOMBRES.put("S",  "Socios Elfec-Comteco-Semapa");
        NOMBRES.put("F",  "Socios Fraccionamiento");
        NOMBRES.put("P",  "Socios Plataforma");
        NOMBRES.put("R",  "Renta Dignidad");
        NOMBRES.put("PC", "Preferente Ahorro-Crédito");
        NOMBRES.put("PS", "Preferente Elfec-Comteco-Semapa");
        NOMBRES.put("PA", "Preferente Semapa");
        NOMBRES.put("PP", "Preferente Plataforma");
    }

    private static final Set<String> CODIGOS_PREFERENTES = Set.of("PC","PS","PA","PP");
    private static final Set<String> CODIGOS_PLATAFORMA  = Set.of("P","PP");
    public static final double UMBRAL_MONTO_MAYOR = 100_000.0;

    private static final Map<String, int[]> TIEMPOS = new LinkedHashMap<>();
    static {
        TIEMPOS.put("C",  new int[]{3,15}); TIEMPOS.put("A",  new int[]{2,12});
        TIEMPOS.put("S",  new int[]{2,15}); TIEMPOS.put("F",  new int[]{4,30});
        TIEMPOS.put("P",  new int[]{1,10}); TIEMPOS.put("R",  new int[]{2,15});
        TIEMPOS.put("PC", new int[]{1,8});  TIEMPOS.put("PS", new int[]{1,8});
        TIEMPOS.put("PA", new int[]{1,8});  TIEMPOS.put("PP", new int[]{1,8});
    }

    private static final Map<String, double[]> MONTOS = new LinkedHashMap<>();
    static {
        MONTOS.put("C",  new double[]{500,    200000});
        MONTOS.put("A",  new double[]{30,     200});
        MONTOS.put("S",  new double[]{20,     300});
        MONTOS.put("F",  new double[]{200,    5000});
        MONTOS.put("P",  new double[]{50,     500});
        MONTOS.put("R",  new double[]{250,    250});
        MONTOS.put("PC", new double[]{500,    200000});
        MONTOS.put("PS", new double[]{20,     300});
        MONTOS.put("PA", new double[]{30,     200});
        MONTOS.put("PP", new double[]{50,     500});
    }

    private final AtomicInteger contadorSocios = new AtomicInteger(0);
    private final Map<String, AtomicInteger> contadoresFicha = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private Map<String, Double>  probabilidades    = new LinkedHashMap<>();
    private ConfiguracionMultiServicio configMulti  = new ConfiguracionMultiServicio();
    private List<ServicioFinanciero> servicios      = new ArrayList<>();

    public GeneradorSociosService() {
        NOMBRES.keySet().forEach(k -> contadoresFicha.put(k, new AtomicInteger(0)));
    }

    public void setConfiguracion(ConfiguracionCooperativa cfg) {
        if (cfg == null) return;
        this.probabilidades = new LinkedHashMap<>(cfg.getProbPorCodigo());
        probabilidades.keySet().forEach(k -> contadoresFicha.putIfAbsent(k, new AtomicInteger(0)));
    }

    public void setServicios(List<ServicioFinanciero> svcs) {
        this.servicios = svcs != null ? new ArrayList<>(svcs) : new ArrayList<>();
    }

    public void setConfiguracionMultiServicio(ConfiguracionMultiServicio c) {
        this.configMulti = c != null ? c : new ConfiguracionMultiServicio();
    }

    public Socio generarSocio(long tiempoLlegada) {
        int    numSocio  = contadorSocios.incrementAndGet();
        String codigo    = sortearCodigo();
        int    numFicha  = contadoresFicha.computeIfAbsent(codigo, k -> new AtomicInteger(0))
                                          .incrementAndGet();
        String ficha     = String.format("%s%03d", codigo, numFicha);

        boolean preferente = CODIGOS_PREFERENTES.contains(codigo);
        boolean soloPlataforma = CODIGOS_PLATAFORMA.contains(codigo);

        Socio socio = new Socio();
        socio.setId("SOC-" + String.format("%05d", numSocio));
        socio.setFicha(ficha);
        socio.setTipoCajaDestino(soloPlataforma ? "PLATAFORMA" : "GENERAL");
        socio.setTiempoLlegada(tiempoLlegada);
        socio.setEsPreferente(preferente);
        socio.setPrioridad(preferente ? 1 : 3);
        socio.setAtendida(false);

        ServicioFinanciero svc = elegirServicio(codigo);
        socio.setServicio(svc);

        int[] rango = TIEMPOS.getOrDefault(codigo, new int[]{2,10});
        if (svc != null) rango = new int[]{ svc.getDuracionMinima(), svc.getDuracionMaxima() };
        int dur = rango[0] + random.nextInt(Math.max(1, rango[1]-rango[0]+1));
        socio.setDuracionEstimada(dur);

        double monto = calcularMonto(codigo, svc);
        socio.setMonto(monto);

        if (monto >= UMBRAL_MONTO_MAYOR && !soloPlataforma) {
            socio.setTipoCajaDestino("MM");
        }

        return socio;
    }

    private String sortearCodigo() {
        if (probabilidades.isEmpty()) return "C";
        double r = random.nextDouble(), acum = 0.0;
        for (Map.Entry<String, Double> e : probabilidades.entrySet()) {
            acum += e.getValue();
            if (r < acum) return e.getKey();
        }
        return probabilidades.keySet().iterator().next();
    }

    private ServicioFinanciero elegirServicio(String codigo) {
        List<ServicioFinanciero> compatibles = servicios.stream()
                .filter(s -> s.isActivo() && codigo.equals(s.getTipoCajaRequerido()))
                .collect(Collectors.toList());
        if (!compatibles.isEmpty()) return compatibles.get(random.nextInt(compatibles.size()));
        return null;
    }

    private double calcularMonto(String codigo, ServicioFinanciero svc) {
        if (svc != null && svc.getMontoMaximo() > 0) {
            double rango = svc.getMontoMaximo() - svc.getMontoMinimo();
            return svc.getMontoMinimo() + random.nextDouble() * Math.max(0, rango);
        }
        double[] m = MONTOS.getOrDefault(codigo, new double[]{50, 500});
        return m[0] + random.nextDouble() * (m[1] - m[0]);
    }

    public void reiniciar() { contadorSocios.set(0); contadoresFicha.values().forEach(c -> c.set(0)); }
    public int getTotalGenerados() { return contadorSocios.get(); }

    public static boolean esCodigoPlataforma(String codigo) { return CODIGOS_PLATAFORMA.contains(codigo); }
    public static boolean esPreferente(String codigo)       { return CODIGOS_PREFERENTES.contains(codigo); }
    public static String getNombreCodigo(String codigo)     { return NOMBRES.getOrDefault(codigo, codigo); }
}