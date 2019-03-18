package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.BoundingBox;

public class RemoveBoundingBoxReceived {
    private final int dimensionId;
    private final BoundingBox key;

    public RemoveBoundingBoxReceived(int dimensionId, BoundingBox key) {
        this.dimensionId = dimensionId;
        this.key = key;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public BoundingBox getKey() {
        return key;
    }
}
