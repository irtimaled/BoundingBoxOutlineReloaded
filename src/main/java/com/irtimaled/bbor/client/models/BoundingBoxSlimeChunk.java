package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxSlimeChunk extends BoundingBoxCuboid {
    private BoundingBoxSlimeChunk(Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.SlimeChunks);
    }

    public static BoundingBoxSlimeChunk from(Coords minCoords, Coords maxCoords) {
        return new BoundingBoxSlimeChunk(minCoords, maxCoords);
    }
}
