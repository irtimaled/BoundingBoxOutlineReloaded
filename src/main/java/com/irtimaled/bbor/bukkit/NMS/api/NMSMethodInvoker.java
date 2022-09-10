package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class NMSMethodInvoker {

    private final Method method;
    private final Class<?> nmsClass;

    public NMSMethodInvoker(@NotNull NMSMethodDescribe describe) throws NoSuchMethodException {
        this.nmsClass = NMSHelper.getNMSClass(describe.className());
        this.method = describe.getMethod();
        this.method.setAccessible(true);
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
