package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxWorldSpawn extends BoundingBoxCuboid {
    private BoundingBoxWorldSpawn(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        super(minCoords, maxCoords, type);
    }

    public static BoundingBoxWorldSpawn from(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        return new BoundingBoxWorldSpawn(minCoords, maxCoords, type);
    }
}
