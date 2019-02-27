package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.dimension.DimensionType;

public class RemoveBoundingBoxReceived {
    private final DimensionType dimensionType;
    private final BoundingBox key;

    public RemoveBoundingBoxReceived(DimensionType dimensionType, BoundingBox key) {
        this.dimensionType = dimensionType;
        this.key = key;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public BoundingBox getKey() {
        return key;
    }
}
