package com.irtimaled.bbor.common.events;

import net.minecraft.village.Village;
import net.minecraft.world.dimension.DimensionType;

public class VillageUpdated {
    private final DimensionType dimensionType;
    private final Village village;

    public VillageUpdated(DimensionType dimensionType, Village village) {
        this.dimensionType = dimensionType;
        this.village = village;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public Village getVillage() {
        return village;
    }
}
