package com.irtimaled.bbor.common.models;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.World;

import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<MinecraftKey, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(ResourceKey<?> dimensionType) {
        return from(dimensionType.b());
    }

    public static DimensionId from(MinecraftKey value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(World.e);

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
