package com.irtimaled.bbor;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static <T, R> R getPrivateValue(Class<T> instanceClass, T instance, int fieldIndex) {
        try {
            Field f = instanceClass.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            Object value = f.get(instance);
            return (R) value;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T, R> R getPrivateValue(Class<T> instanceClass, T instance, int fieldIndex, Class<R> resultClass) {
        try {
            Field f = instanceClass.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            Object value = f.get(instance);
            if (resultClass.isAssignableFrom(value.getClass()))
                return (R) value;
        } catch (Exception e) {
        }
        return null;
    }
}
