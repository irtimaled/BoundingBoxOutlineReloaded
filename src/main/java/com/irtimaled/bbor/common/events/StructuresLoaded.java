package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;

import java.util.Map;

public class StructuresLoaded {

    private final Map<String, Object> structures;
    private final DimensionId dimensionId;

    public StructuresLoaded(Map<String, Object> structures, DimensionId dimensionId) {
        this.structures = structures;
        this.dimensionId = dimensionId;
    }

    public Map<String, Object> getStructures() {
        return structures;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }
}
