package com.irtimaled.bbor.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectionHelper {
    public static <T, R> Function<T, R> getPrivateFieldGetter(Class<?> clazz, Type fieldType, Type... genericTypeArguments) {
        Field field = getGenericField(clazz, fieldType, genericTypeArguments);
        if (field == null) return obj -> null;

        field.setAccessible(true);
        return obj -> {
            try {
                return (R) field.get(obj);
            } catch (IllegalAccessException ignored) {
                return null;
            }
        };
    }

    public static Field getGenericField(Class<?> clazz, Type fieldType, Type[] genericTypeArguments) {
        Field field = findField(clazz, fieldType, genericTypeArguments);
        return field != null ? field : findField(clazz, fieldType, null);
    }

    private static Field findField(Class<?> clazz, Type fieldType, Type[] genericTypeArguments) {
        for (Field field : clazz.getDeclaredFields()) {
            Type type = field.getGenericType();
            ParameterizedType genericType = TypeHelper.as(type, ParameterizedType.class);
            if (genericType == null) {
                if (type != fieldType || genericTypeArguments.length > 0) continue;
                return field;
            }

            Type rawType = genericType.getRawType();
            if (rawType != fieldType) continue;

            if (genericTypeArguments == null) return field;

            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (!typesMatch(genericTypeArguments, actualTypeArguments)) continue;

            return field;
        }
        return null;
    }

    private static boolean typesMatch(Type[] left, Type[] right) {
        if (left.length != right.length) return false;

        for (int index = 0; index < right.length; index++) {
            if (right[index] != left[index]) {
                return false;
            }
        }
        return true;
    }

    public static <T, R, S> BiFunction<T, R, S> getPrivateInstanceBuilder(Class<S> clazz, Class<T> parameter1, Class<R> parameter2) {
        Constructor<S> constructor = findConstructor(clazz, parameter1, parameter2);
        if (constructor == null) return (obj1, obj2) -> null;

        constructor.setAccessible(true);
        return (obj1, obj2) -> {
            try {
                return (S) constructor.newInstance(obj1, obj2);
            } catch (Exception ignored) {
                return null;
            }
        };
    }

    private static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameters) {
        try {
            return clazz.getDeclaredConstructor(parameters);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
