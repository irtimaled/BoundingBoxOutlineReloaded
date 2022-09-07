package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public record NMSMethodDescribe(NMSClassName className, String methodName, Class<?>[] parameterTypes) {

    @NotNull
    public Method getMethod() throws NoSuchMethodException {
        return getMethod(this);
    }

    @NotNull
    public static Method getMethod(@NotNull NMSMethodDescribe describe) throws NoSuchMethodException {
        Class<?> clazz = NMSHelper.getNMSClass(describe.className());

        do {
            try {
                return clazz.getDeclaredMethod(describe.methodName(), describe.parameterTypes());
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz.getSuperclass() != null);

        throw new NoSuchMethodException(describe.methodName());
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static NMSMethodDescribe of(NMSClassName className, String methodName, Class<?>... parameterTypes) {
        return new NMSMethodDescribe(className, methodName, parameterTypes);
    }
}
