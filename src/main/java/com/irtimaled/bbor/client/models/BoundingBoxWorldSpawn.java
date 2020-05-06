package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxWorldSpawn extends BoundingBoxCuboid {
    private BoundingBoxWorldSpawn(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        super(minCoords, maxCoords, type);
    }

    public static BoundingBoxWorldSpawn from(Coords minCoords, Coords maxCoords, BoundingBoxType type) {
        return new BoundingBoxWorldSpawn(minCoords, maxCoords, type);
    }
}
