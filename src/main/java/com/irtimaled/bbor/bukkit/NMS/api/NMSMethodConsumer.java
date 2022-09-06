package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class NMSMethodConsumer {

    private final Method method;
    private final Object object;
    private final Class<?> nmsClass;

    public NMSMethodConsumer(@NotNull NMSMethodDescribe describe, @NotNull Object object) throws NoSuchMethodException {
        this.object = object;
        this.nmsClass = NMSHelper.getNMSClass(describe.className());
        this.method = nmsClass.getDeclaredMethod(describe.methodName(), describe.parameterTypes());
        this.method.setAccessible(true);
    }

    public void accept(Object parameter) {
        try {
            method.invoke(nmsClass.cast(object), parameter);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
