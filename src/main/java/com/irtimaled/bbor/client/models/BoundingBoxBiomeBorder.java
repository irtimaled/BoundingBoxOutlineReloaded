package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.BiomeBorderRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxBiomeBorder extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxBiomeBorder> RENDERER = CommonInterop.registerRenderer(BoundingBoxBiomeBorder.class, () -> new BiomeBorderRenderer());

    private final Coords coords;
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;
    private final boolean up;
    private final boolean down;

    public BoundingBoxBiomeBorder(Coords coords, boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
        super(BoundingBoxType.BiomeBorder);
        this.coords = coords;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.up = up;
        this.down = down;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return coords.getX() >= minX &&
                coords.getZ() >= minZ &&
                coords.getX() <= maxX &&
                coords.getZ() <= maxZ;
    }

    @Override
    protected double getDistanceX(double x) {
        return x - coords.getX();
    }

    @Override
    protected double getDistanceY(double y) {
        return y - coords.getY();
    }

    @Override
    protected double getDistanceZ(double z) {
        return z - coords.getZ();
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

    public boolean renderUp() {
        return up;
    }

    public boolean renderDown() {
        return down;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }

    @Override
    public boolean isVisibleCulling() {
        return RenderCulling.isVisibleCulling(coords.getX(), coords.getY(), coords.getZ(), coords.getX() + 1, coords.getY() + 1, coords.getZ() + 1);
    }
}
