package com.supermercado.infrastructure.adapter.event;

import com.supermercado.application.supermercado.port.IEventPublisher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventBusAdapter implements IEventPublisher {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "EventBus-Thread");
        t.setDaemon(true);
        return t;
    });

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add((Consumer<Object>) listener);
    }

    @Override
    public void publish(Object event) {
        List<Consumer<Object>> consumers = listeners.get(event.getClass());
        if (consumers != null) {
            executor.submit(() -> {
                for (Consumer<Object> c : consumers) {
                    try {
                        c.accept(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}