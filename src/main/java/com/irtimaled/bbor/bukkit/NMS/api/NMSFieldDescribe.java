package com.irtimaled.bbor.bukkit.NMS.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record NMSFieldDescribe(NMSClassName className, String fieldName) {
    @NotNull
    @Contract("_, _ -> new")
    public static NMSFieldDescribe of(NMSClassName className, String fieldName) {
        return new NMSFieldDescribe(className, fieldName);
    }
}
