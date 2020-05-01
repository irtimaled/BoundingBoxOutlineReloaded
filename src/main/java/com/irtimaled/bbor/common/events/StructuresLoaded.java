package com.irtimaled.bbor.common.events;

import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Map;

public class StructuresLoaded {
    private final Map<String, StructureStart> structures;
    private final int dimensionId;

    public StructuresLoaded(Map<String, StructureStart> structures, int dimensionId) {
        this.structures = structures;
        this.dimensionId = dimensionId;
    }

    public Map<String, StructureStart> getStructures() {
        return structures;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
