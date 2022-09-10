package com.irtimaled.bbor.bukkit.NMS.version;

import com.irtimaled.bbor.bukkit.NMS.api.INMSClass;
import com.irtimaled.bbor.bukkit.NMS.api.NMSClassName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseNMSClass implements INMSClass {

    public BaseNMSClass() throws ClassNotFoundException {

    }

    protected final Map<NMSClassName, Class<?>> nmsClassCache = new HashMap<>();

    protected void addClassCache(NMSClassName className, String classPath) throws ClassNotFoundException {
        nmsClassCache.put(className, Class.forName(classPath));
    }

    @NotNull
    @Override
    public Class<?> getNMSClass(@NotNull NMSClassName name) {
        return nmsClassCache.get(name);
    }

    @Nullable
    @Override
    public Object cast(@NotNull NMSClassName name, @Nullable Object object) {
        return getNMSClass(name).cast(object);
    }
}
