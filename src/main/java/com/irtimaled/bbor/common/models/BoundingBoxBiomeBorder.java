package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxBiomeBorder extends AbstractBoundingBox {
    private final Coords coords;
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;

    private BoundingBoxBiomeBorder(Coords coords, boolean north, boolean east, boolean south, boolean west) {
        super(BoundingBoxType.Custom);
        this.coords = coords;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public static BoundingBoxBiomeBorder from(Coords coords, boolean north, boolean east, boolean south, boolean west) {
        return new BoundingBoxBiomeBorder(coords, north, east, south, west);
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return coords.getX() >= minX &&
                coords.getZ() >= minZ &&
                coords.getX() <= maxX &&
                coords.getZ() <= maxZ;
    }

    public Coords getCoords() {
        return coords;
    }

    public boolean renderNorth() {
        return north;
    }

    public boolean renderEast() {
        return east;
    }

    public boolean renderSouth() {
        return south;
    }

    public boolean renderWest() {
        return west;
    }

}
