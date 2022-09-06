package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class NMSMethodInvoker {

    private final Method method;
    private final Class<?> nmsClass;

    public NMSMethodInvoker(@NotNull NMSMethodDescribe describe) throws NoSuchMethodException {
        this.nmsClass = NMSHelper.getNMSClass(describe.className());
        this.method = getMethod(describe);
        this.method.setAccessible(true);
    }

    @NotNull
    private static Method getMethod(@NotNull NMSMethodDescribe describe) throws NoSuchMethodException {
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

    public Object invoke(Object obj, Object... parameters) {
        try {
            return method.invoke(nmsClass.cast(obj), parameters);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
