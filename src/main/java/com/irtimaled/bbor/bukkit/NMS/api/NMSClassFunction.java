package com.irtimaled.bbor.bukkit.NMS.api;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

public class NMSClassFunction implements Cloneable {

    private final Constructor<?> constructor;

    public NMSClassFunction(@NotNull NMSFunctionDescribe describe) throws NoSuchMethodException {
        this.constructor = NMSHelper.getNMSClass(describe.className()).getConstructor(describe.parameterTypes());
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

    @Override
    public NMSClassFunction clone()  {
        try {
            return (NMSClassFunction) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
