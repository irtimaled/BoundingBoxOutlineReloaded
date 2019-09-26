package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxBeacon extends BoundingBoxCuboid {
    private final Coords coords;

    private BoundingBoxBeacon(Coords coords, Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.Beacon);
        this.coords = coords;
    }

    public static BoundingBoxBeacon from(Coords coords, int level) {
        int range = 10 + (10 * level);
        Coords minCoords = new Coords(coords.getX() - range, coords.getY() - range, coords.getZ() - range);
        Coords maxCoords = new Coords(coords.getX() + range, 324 + range, coords.getZ() + range);
        return new BoundingBoxBeacon(coords, minCoords, maxCoords);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + coords.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BoundingBoxBeacon other = (BoundingBoxBeacon) obj;
        return coords.equals(other.coords);
    }

    @Override
    public String toString() {
        return "(" + coords.toString() + ")";
    }

    public Coords getCoords() {
        return coords;
    }
}
