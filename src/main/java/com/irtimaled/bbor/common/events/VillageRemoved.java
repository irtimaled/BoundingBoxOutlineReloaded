package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.dimension.DimensionType;

public class VillageRemoved {
    private final DimensionType dimensionType;
    private final BoundingBox boundingBox;

    public VillageRemoved(DimensionType dimensionType, BoundingBox boundingBox) {
        this.dimensionType = dimensionType;
        this.boundingBox = boundingBox;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
