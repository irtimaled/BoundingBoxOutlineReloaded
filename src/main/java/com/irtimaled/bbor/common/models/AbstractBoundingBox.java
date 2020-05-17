package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public abstract class AbstractBoundingBox {
    private final BoundingBoxType type;

    protected AbstractBoundingBox(BoundingBoxType type) {
        this.type = type;
    }

    public abstract Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ);

    public BoundingBoxType getType() {
        return type;
    }
}
