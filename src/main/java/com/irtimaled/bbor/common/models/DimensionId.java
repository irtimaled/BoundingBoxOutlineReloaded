package com.irtimaled.bbor.common.models;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class DimensionId {
    private static final Map<Identifier, DimensionId> dimensionIdMap = new HashMap<>();

    public static DimensionId from(World world) {
        Identifier value;
        value = world.getRegistryKey().getValue();
        return from(value);
    }

    public static DimensionId from(Identifier value) {
        return dimensionIdMap.computeIfAbsent(value, DimensionId::new);
    }

    public static DimensionId OVERWORLD = DimensionId.from(World.OVERWORLD.getValue());


    private final Identifier value;

    public DimensionId(Identifier value) {
        this.value = value;
    }

    public Identifier getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
