package com.irtimaled.bbor.common.models;

import net.minecraft.server.v1_16_R2.DimensionManager;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import net.minecraft.server.v1_16_R2.Registry;
import net.minecraft.server.v1_16_R2.ResourceKey;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<MinecraftKey, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(ResourceKey<?> dimensionType) {
        return from(dimensionType.a());
    }

    public static DimensionId from(MinecraftKey value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(DimensionManager.OVERWORLD);

    private final MinecraftKey value;

    public DimensionId(MinecraftKey value) {
        this.value = value;
    }

    public MinecraftKey getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
