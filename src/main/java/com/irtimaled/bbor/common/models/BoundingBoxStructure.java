package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxStructure extends BoundingBox {
    private BoundingBoxStructure(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        super(minCoords, maxCoords, type);
    }

    public static BoundingBoxStructure from(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        return new BoundingBoxStructure(minCoords, maxCoords, type);
    }
}
