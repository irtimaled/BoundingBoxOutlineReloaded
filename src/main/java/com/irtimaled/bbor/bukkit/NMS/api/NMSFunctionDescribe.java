package com.irtimaled.bbor.bukkit.NMS.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record NMSFunctionDescribe(NMSClassName className, Class<?>[] parameterTypes) {
    @NotNull
    @Contract("_, _ -> new")
    public static NMSFunctionDescribe of(NMSClassName className, Class<?>... parameterTypes) {
        return new NMSFunctionDescribe(className, parameterTypes);
    }
}
