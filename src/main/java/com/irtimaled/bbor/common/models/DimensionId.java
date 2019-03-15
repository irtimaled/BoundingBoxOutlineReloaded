package com.irtimaled.bbor.common.models;

import net.minecraft.server.v1_14_R1.DimensionManager;
import net.minecraft.server.v1_14_R1.MinecraftKey;

import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<MinecraftKey, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(DimensionManager dimensionType) {
        return from(DimensionManager.a(dimensionType));
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
