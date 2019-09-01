package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxMobSpawner extends BoundingBoxCuboid {
    private final Coords coords;

    private BoundingBoxMobSpawner(Coords coords, Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.MobSpawner);
        this.coords = coords;
    }

    public static BoundingBoxMobSpawner from(Coords coords) {
        Coords minCoords = new Coords(coords.getX() - 5,
                coords.getY() - 1,
                coords.getZ() - 5);
        Coords maxCoords = new Coords(coords.getX() + 5,
                coords.getY() + 2,
                coords.getZ() + 5);
        return new BoundingBoxMobSpawner(coords, minCoords, maxCoords);
    }

    @Override
    public String toString() {
        return "(" + coords.toString() + ")";
    }

    public Coords getCoords() {
        return coords;
    }
}
