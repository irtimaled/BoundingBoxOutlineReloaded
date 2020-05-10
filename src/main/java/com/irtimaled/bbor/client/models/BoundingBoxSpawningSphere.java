package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxSphere;
import com.irtimaled.bbor.common.models.Point;

public class BoundingBoxSpawningSphere extends BoundingBoxSphere {
    public static final int SAFE_RADIUS = 24;
    public static final int SPAWN_RADIUS = 128;

    private Integer spawnableCount;

    public BoundingBoxSpawningSphere(Point point) {
        super(point, SPAWN_RADIUS, BoundingBoxType.AFKSphere);
    }

    public boolean isWithinSphere(Point point) {
        return this.getPoint().getDistance(point) <= getRadius() + 0.5D;
    }

    public void setSpawnableCount(int count) {
        this.spawnableCount = count;
    }

    public Integer getSpawnableSpacesCount() {
        return this.spawnableCount;
    }
}
