package com.irtimaled.bbor;

import com.irtimaled.bbor.common.TypeHelper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ReflectionHelper {
    public static <T, R> Function<T, R> getPrivateFieldGetter(Class<?> clazz, Type fieldType, Type... genericTypeArguments) {
        Field field = findField(clazz, fieldType, genericTypeArguments);
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

            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length != genericTypeArguments.length) continue;

            for (int typeIndex = 0; typeIndex < actualTypeArguments.length; typeIndex++) {
                if (actualTypeArguments[typeIndex] != genericTypeArguments[typeIndex]) return null;
            }

            return field;
        }
        return null;
    }
}
