package com.irtimaled.bbor.common.models;

import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<Identifier, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(DimensionType dimensionType) {
        return from(DimensionType.getId(dimensionType));
    }

    public static DimensionId from(Identifier value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(DimensionType.OVERWORLD);
    public static DimensionId NETHER = DimensionId.from(DimensionType.THE_NETHER);

    private final Identifier value;

    public DimensionId(Identifier value) {
        this.value = value;
    }

    public Identifier getValue() {
        return value;
    }

    public DimensionType getDimensionType() {
        return DimensionType.byId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
