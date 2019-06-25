package com.irtimaled.bbor.common.chunkProcessors;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionHelper {
    public static <T, R> R getPrivateValue(Class<T> sourceClass, T instance, Class<R> resultClass) {
        try {
            Field f = getField(sourceClass, resultClass);
            if (f != null) {
                return (R) f.get(instance);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Map<Class, Map<Class, Field>> fieldMap = new HashMap<>();

    private static <T, R> Field getField(Class<T> sourceClass, Class<R> resultClass) {
        Map<Class, Field> map = fieldMap.computeIfAbsent(sourceClass, k -> new HashMap<>());
        Field field = map.get(resultClass);
        if (field == null) {
            field = getFieldUsingReflection(sourceClass, resultClass);
            if (field != null) {
                field.setAccessible(true);
                map.put(resultClass, field);
            }
        }
        return field;
    }

    private static <T, R> Field getFieldUsingReflection(Class<T> sourceClass, Class<R> resultClass) {
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(resultClass))
                return field;
        }
        for (Field field : fields) {
            if (resultClass.isAssignableFrom(field.getType()))
                return field;
        }
        return null;
    }
}