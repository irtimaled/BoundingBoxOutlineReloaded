package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import net.minecraft.world.dimension.DimensionType;

public class VillageRemoved {
    private final DimensionType dimensionType;
    private final BoundingBoxVillage village;

    public VillageRemoved(DimensionType dimensionType, BoundingBoxVillage village) {

        this.dimensionType = dimensionType;
        this.village = village;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public BoundingBoxVillage getVillage() {
        return village;
    }
}
