package com.irtimaled.bbor.server;

import java.util.function.Consumer;

public interface ThrowableConsumer<T> extends Consumer<T> {
    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    }

    void acceptThrows(T elem) throws Throwable;
}
