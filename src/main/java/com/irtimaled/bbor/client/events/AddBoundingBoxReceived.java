package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.dimension.DimensionType;

import java.util.Set;

public class AddBoundingBoxReceived {
    private final DimensionType dimensionType;
    private final BoundingBox key;
    private final Set<BoundingBox> boundingBoxes;

    public AddBoundingBoxReceived(DimensionType dimensionType, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        this.dimensionType = dimensionType;
        this.key = key;
        this.boundingBoxes = boundingBoxes;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public BoundingBox getKey() {
        return key;
    }

    public Set<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }
}
