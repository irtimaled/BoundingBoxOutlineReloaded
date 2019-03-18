package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.BoundingBox;

import java.util.Set;

public class AddBoundingBoxReceived {
    private final int dimensionId;
    private final BoundingBox key;
    private final Set<BoundingBox> boundingBoxes;

    public AddBoundingBoxReceived(int dimensionId, BoundingBox key, Set<BoundingBox> boundingBoxes) {
        this.dimensionId = dimensionId;
        this.key = key;
        this.boundingBoxes = boundingBoxes;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public BoundingBox getKey() {
        return key;
    }

    public Set<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }
}
