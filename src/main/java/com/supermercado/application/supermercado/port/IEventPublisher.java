package com.supermercado.application.supermercado.port;

/**
 * Puerto de salida para la publicación de eventos de dominio.
 * Permite desacoplar la lógica de negocio de la notificación a los adaptadores.
 */
public interface IEventPublisher {
    void publish(Object event);
}