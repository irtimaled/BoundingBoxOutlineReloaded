package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxSpawningSphere extends BoundingBoxSphere {
    public static final int SAFE_RADIUS = 24;
    public static final int SPAWN_RADIUS = 128;

    private Integer spawnableCount;

    public BoundingBoxSpawningSphere(Coords coords, double xOffset, double yOffset, double zOffset) {
        super(coords, 128, BoundingBoxType.AFKSphere);
        setCenterOffsets(xOffset, yOffset, zOffset);
    }

    public boolean isCenter(Coords coords, double xOffset, double yOffset, double zOffset) {
        return this.getCenter().equals(coords) &&
                this.getCenterOffsetX() == xOffset &&
                this.getCenterOffsetY() == yOffset &&
                this.getCenterOffsetZ() == zOffset;
    }

    public void setSpawnableCount(int count) {
        this.spawnableCount = count;
    }

    public Integer getSpawnableSpacesCount() {
        return this.spawnableCount;
    }
}
