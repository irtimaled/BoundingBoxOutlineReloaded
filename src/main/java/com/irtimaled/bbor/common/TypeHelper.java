package com.irtimaled.bbor.common;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TypeHelper {
    public static <T> T as(Object value, Class<T> clazz) {
        return clazz.isInstance(value) ? (T) value : null;
    }

    public static <T> T as(Object value, Class<T> clazz, Supplier<T> defaultValueFunc) {
        return clazz.isInstance(value) ? (T) value : defaultValueFunc.get();
    }

    public static <T> void doIfType(Object value, Class<T> clazz, Consumer<T> consumer) {
        T typedValue = as(value, clazz);
        if (typedValue == null) return;
        consumer.accept(typedValue);
    }

    public static int combineHashCodes(int... hashCodes) {
        final int prime = 31;
        int result = 0;
        for (int hashCode : hashCodes) {
            result = prime * result + hashCode;
        }
        return result;
    }
}
