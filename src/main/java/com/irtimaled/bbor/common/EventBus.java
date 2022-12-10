package com.irtimaled.bbor.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {

    private static final Map<Class<?>, Consumer<?>> handlers = new HashMap<>();

    public static <evt> void publish(evt event) {
        if (event == null) return;

        Class<?> clazz = event.getClass();
        Consumer<?> handler = handlers.get(clazz);
        if (handler == null) return;

        ((Consumer<evt>) handler).accept(event);
    }

    public static <evt> void subscribe(Class<evt> clazz, Consumer<evt> consumer) {
        handlers.put(clazz, consumer);
    }
}
