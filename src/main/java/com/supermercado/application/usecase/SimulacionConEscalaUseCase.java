package com.supermercado.application.usecase;

import com.supermercado.application.dto.ConfiguracionDTO;
import com.supermercado.application.port.ILogService;
import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UseCase que ejecuta la simulación con escala de tiempo.
 * Versión simplificada y estable para pruebas.
 */
public class SimulacionConEscalaUseCase {

    private final ILogService logService;
    private volatile boolean ejecutando;
    private volatile boolean pausado;
    private Thread simThread;
    private ConfiguracionDTO config;
    private List<Caja> cajas;
    private Random random;
    private int clientesGenerados;

    public SimulacionConEscalaUseCase(ILogService logService) {
        this.logService = logService;
        this.ejecutando = false;
        this.pausado = false;
        this.random = new Random();
        this.cajas = new ArrayList<>();
    }

    public void ejecutar(ConfiguracionDTO config) throws InterruptedException {
        if (config == null) {
            throw new NullPointerException("La configuración no puede ser nula");
        }
        if (ejecutando) {
            throw new IllegalStateException("La simulación ya está en ejecución");
        }

        this.config = config;
        this.ejecutando = true;
        this.pausado = false;
        this.clientesGenerados = 0;
        this.cajas.clear();

        // Crear cajas
        int numRapidas = config.getNumCajasRapidas();
        int numNormales = config.getNumCajasNormales();
        for (int i = 1; i <= numRapidas; i++) {
            cajas.add(new Caja(i, true));
        }
        for (int i = 1; i <= numNormales; i++) {
            cajas.add(new Caja(numRapidas + i, false));
        }

        logService.info("Simulación iniciada con " + cajas.size() + " cajas");

        // Ejecutar la simulación en un hilo separado para no bloquear
        simThread = new Thread(this::runSimulation);
        simThread.start();

        // Esperar a que el hilo termine (o hasta que se detenga)
        simThread.join();
    }

    private void runSimulation() {
        int limiteClientes = config.getLimiteClientes();
        long duracionMs = config.getDuracionRealSegundos() * 1000L;
        long inicio = System.currentTimeMillis();

        while (ejecutando && (System.currentTimeMillis() - inicio) < duracionMs) {
            // Pausa
            while (pausado && ejecutando) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Generar cliente según probabilidad
            if (limiteClientes <= 0 || clientesGenerados < limiteClientes) {
                if (random.nextInt(100) < config.getProbabilidadLlegadaCliente()) {
                    generarCliente();
                }
            }

            // Simular el paso del tiempo (1 tick = 100ms)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Finalizar
        ejecutando = false;
        for (Caja caja : cajas) {
            caja.detener();
        }
        logService.info("Simulación finalizada");
    }

    private void generarCliente() {
        int min = config.getArticulosClienteMin();
        int max = config.getArticulosClienteMax();
        int articulos = min + random.nextInt(max - min + 1);
        Cliente cliente = new Cliente(++clientesGenerados, articulos);

        // Asignar a la caja con menos cola
        Caja mejorCaja = null;
        int minCola = Integer.MAX_VALUE;
        for (Caja caja : cajas) {
            if (caja.getEstado() == com.supermercado.domain.model.EstadoCaja.DETENIDA) continue;
            if (!caja.esRapida() || cliente.esRapido()) {
                int colaSize = caja.getClientesEnCola();
                if (colaSize < minCola) {
                    minCola = colaSize;
                    mejorCaja = caja;
                }
            }
        }
        if (mejorCaja != null) {
            mejorCaja.agregarCliente(cliente);
        }
    }

    public void detener() {
        ejecutando = false;
        pausado = false;
        if (simThread != null && simThread.isAlive()) {
            simThread.interrupt();
            try {
                simThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (Caja caja : cajas) {
            caja.detener();
        }
        logService.info("Simulación detenida");
    }

    public void pausar() {
        if (ejecutando && !pausado) {
            pausado = true;
            logService.info("Simulación pausada");
        }
    }

    public void reanudar() {
        if (ejecutando && pausado) {
            pausado = false;
            logService.info("Simulación reanudada");
        }
    }

    public boolean isEjecutando() {
        return ejecutando;
    }

    public boolean isPausado() {
        return pausado;
    }
}