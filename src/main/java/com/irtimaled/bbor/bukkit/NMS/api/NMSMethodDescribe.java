package com.irtimaled.bbor.bukkit.NMS.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record NMSMethodDescribe(NMSClassName className, String methodName, Class<?>[] parameterTypes) {
    @NotNull
    @Contract("_, _, _ -> new")
    public static NMSMethodDescribe of(NMSClassName className, String methodName, Class<?>... parameterTypes) {
        return new NMSMethodDescribe(className, methodName, parameterTypes);
    }
}
