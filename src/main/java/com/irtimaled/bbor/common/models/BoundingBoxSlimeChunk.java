package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxSlimeChunk extends BoundingBox {
    private BoundingBoxSlimeChunk(Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.SlimeChunks);
    }

    public static BoundingBoxSlimeChunk from(Coords minCoords, Coords maxCoords) {
        return new BoundingBoxSlimeChunk(minCoords, maxCoords);
    }
}
