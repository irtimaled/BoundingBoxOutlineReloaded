package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class NMSMethodConsumer {

    private final Method method;
    private final Object object;
    private final Class<?> nmsClass;

    public NMSMethodConsumer(@NotNull NMSMethodDescribe describe, @Nullable Object object) throws NoSuchMethodException {
        this.object = object;
        this.nmsClass = NMSHelper.getNMSClass(describe.className());
        this.method = nmsClass.getDeclaredMethod(describe.methodName(), describe.parameterTypes());
        this.method.setAccessible(true);
    }

    private NMSMethodConsumer(@NotNull Method method, @NotNull Object object, @NotNull Class<?> nmsClass) {
        this.object = object;
        this.method = method;
        this.nmsClass = nmsClass;
    }

    public void accept(Object parameter) {
        try {
            method.invoke(nmsClass.cast(object), parameter);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public NMSMethodConsumer toNew(Object obj) {
        return new NMSMethodConsumer(method, obj, nmsClass);
    }
}
