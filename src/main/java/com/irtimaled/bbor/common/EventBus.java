package com.irtimaled.bbor.common;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    public static <evt> void publish(evt event) {
        if (event == null) return;

        Class<?> clazz = event.getClass();
        List<Consumer<?>> handlers = EventBus.handlers.get(clazz);
        if (handlers == null) return;
        for (Consumer<?> handler : handlers) {
            ((Consumer<evt>) handler).accept(event);
        }
    }

    public static <evt> void subscribe(Class<evt> clazz, Consumer<evt> consumer) {
        handlers.computeIfAbsent(clazz, k -> new ObjectArrayList<>()).add(consumer);
    }
}
