package com.supermercado.application.usecase;

import com.supermercado.domain.model.Cliente;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Caso de uso: Generar un nuevo cliente con articulos aleatorios.
 *
 * Responsabilidad unica: crear instancias de Cliente con un
 * identificador unico y una cantidad de articulos dentro del
 * rango configurado.
 *
 * NOTA: El contador usa AtomicInteger en lugar de static int
 * para garantizar correcta operacion en entornos multi-hilo
 * y evitar bugs en ejecuciones consecutivas de tests.
 */
public class GenerarClienteUseCase {

    private final AtomicInteger contador;
    private final Random        random;

    public GenerarClienteUseCase() {
        this.contador = new AtomicInteger(0);
        this.random   = new Random();
    }

    /**
     * Genera un nuevo cliente con articulos en el rango [minArticulos, maxArticulos].
     *
     * @param minArticulos minimo de articulos (>= 0)
     * @param maxArticulos maximo de articulos (>= minArticulos)
     * @return nueva instancia de Cliente con ID unico
     * @throws IllegalArgumentException si el rango es invalido
     */
    public Cliente ejecutar(int minArticulos, int maxArticulos) {
        validarRango(minArticulos, maxArticulos);
        int articulos = minArticulos + random.nextInt(maxArticulos - minArticulos + 1);
        return new Cliente(contador.incrementAndGet(), articulos);
    }

    /**
     * Reinicia el contador de clientes generados.
     * Util para iniciar una nueva simulacion desde cero.
     */
    public void reiniciarContador() {
        contador.set(0);
    }

    /**
     * Retorna el total de clientes generados hasta el momento.
     */
    public int getTotalGenerados() {
        return contador.get();
    }

    private void validarRango(int min, int max) {
        if (min < 0) {
            throw new IllegalArgumentException(
                "El minimo de articulos no puede ser negativo: " + min);
        }
        if (max < min) {
            throw new IllegalArgumentException(
                "El maximo (" + max + ") no puede ser menor que el minimo (" + min + ")");
        }
    }
}