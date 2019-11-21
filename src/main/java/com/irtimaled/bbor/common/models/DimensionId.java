package com.irtimaled.bbor.common.models;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<ResourceLocation, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(DimensionType dimensionType) {
        return from(DimensionType.getKey(dimensionType));
    }

    public static DimensionId from(ResourceLocation value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(DimensionType.OVERWORLD);
    public static DimensionId NETHER = DimensionId.from(DimensionType.THE_NETHER);

    private final ResourceLocation value;

    public DimensionId(ResourceLocation value) {
        this.value = value;
    }

    public ResourceLocation getValue() {
        return value;
    }

    public DimensionType getDimensionType() {
        return DimensionType.byName(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
