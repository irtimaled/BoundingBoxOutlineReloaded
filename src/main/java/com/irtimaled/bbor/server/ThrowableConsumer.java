package com.irtimaled.bbor.server;

import com.irtimaled.bbor.common.TypeHelper;

import java.util.function.Consumer;

public interface ThrowableConsumer<T> extends Consumer<T> {
    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final Throwable t) {
            throw TypeHelper.as(t, RuntimeException.class, () -> new RuntimeException(t));
        }
    }

    void acceptThrows(T elem) throws Throwable;
}
