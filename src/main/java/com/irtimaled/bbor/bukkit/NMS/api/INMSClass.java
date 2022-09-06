package com.irtimaled.bbor.bukkit.NMS.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INMSClass {

    @NotNull
    Class<?> getNMSClass(@NotNull NMSClassName name);

    @Nullable
    @Contract("_, null -> null")
    Object cast(@NotNull NMSClassName name, @Nullable Object object);

}
