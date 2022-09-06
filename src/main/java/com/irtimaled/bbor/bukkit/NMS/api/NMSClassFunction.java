package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;

import java.lang.reflect.Constructor;

public class NMSClassFunction {

    private final Constructor<?> constructor;

    public NMSClassFunction(NMSClassName className, Class<?>... parameterTypes) throws NoSuchMethodException {
        this.constructor = NMSHelper.getNMSClass(className).getConstructor(parameterTypes);
        this.constructor.setAccessible(true);
    }

    public Object apply(Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
