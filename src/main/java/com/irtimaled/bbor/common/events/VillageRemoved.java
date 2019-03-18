package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;

public class VillageRemoved {
    private final int dimensionId;
    private final BoundingBoxVillage village;

    public VillageRemoved(int dimensionId, BoundingBoxVillage village) {
        this.dimensionId = dimensionId;
        this.village = village;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public BoundingBoxVillage getVillage() {
        return village;
    }
}
