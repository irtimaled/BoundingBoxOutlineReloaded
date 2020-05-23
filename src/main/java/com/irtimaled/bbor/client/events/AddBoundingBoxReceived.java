package com.irtimaled.bbor.client.events;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.Set;

public class AddBoundingBoxReceived {
    private final DimensionId dimensionId;
    private final AbstractBoundingBox key;
    private final Set<AbstractBoundingBox> boundingBoxes;

    public AddBoundingBoxReceived(DimensionId dimensionId, AbstractBoundingBox key, Set<AbstractBoundingBox> boundingBoxes) {
        this.dimensionId = dimensionId;
        this.key = key;
        this.boundingBoxes = boundingBoxes;
    }

    public DimensionId getDimensionId() {
        return dimensionId;
    }

    public AbstractBoundingBox getKey() {
        return key;
    }

    public Set<AbstractBoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }
}
