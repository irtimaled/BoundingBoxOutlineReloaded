package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;

public class RemoveBoundingBoxReceived {
    private final int dimensionId;
    private final AbstractBoundingBox key;

    public RemoveBoundingBoxReceived(int dimensionId, AbstractBoundingBox key) {
        this.dimensionId = dimensionId;
        this.key = key;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public AbstractBoundingBox getKey() {
        return key;
    }
}
