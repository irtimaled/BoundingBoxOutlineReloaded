package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Map;

public class StructuresLoaded {
    private final Map<String, StructureStart<?>> structures;
    private final DimensionId dimensionId;

    public StructuresLoaded(Map<String, StructureStart<?>> structures, DimensionId dimensionId) {
        this.structures = structures;
        this.dimensionId = dimensionId;
    }

    public Map<String, StructureStart<?>> getStructures() {
        return structures;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }
}
