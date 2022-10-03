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

    private static final byte MASK_NORTH = 1 << 0;
    private static final byte MASK_EAST = 1 << 1;
    private static final byte MASK_SOUTH = 1 << 2;
    private static final byte MASK_WEST = 1 << 3;
    private static final byte MASK_UP = 1 << 4;
    private static final byte MASK_DOWN = 1 << 5;

    private final Coords coords;
//    private final boolean north;
//    private final boolean east;
//    private final boolean south;
//    private final boolean west;
//    private final boolean up;
//    private final boolean down;
    private final byte packed;
    private final int biomeId;

    private static byte toByte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public BoundingBoxBiomeBorder(Coords coords, boolean north, boolean east, boolean south, boolean west, boolean up, boolean down, int biomeId) {
        super(BoundingBoxType.BiomeBorder);
        this.coords = coords;
        this.biomeId = biomeId;
        this.packed = (byte) ((toByte(north) << 0) | (toByte(east) << 1) | (toByte(south) << 2) | (toByte(west) << 3) | (toByte(up) << 4) | (toByte(down) << 5));
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
        return (packed & MASK_NORTH) != 0;
    }

    public boolean renderEast() {
        return (packed & MASK_EAST) != 0;
    }

    public boolean renderSouth() {
        return (packed & MASK_SOUTH) != 0;
    }

    public boolean renderWest() {
        return (packed & MASK_WEST) != 0;
    }

    public boolean renderUp() {
        return (packed & MASK_UP) != 0;
    }

    public boolean renderDown() {
        return (packed & MASK_DOWN) != 0;
    }

    public boolean hasRender() {
        return packed != 0;
    }

    public int getBiomeId() {
        return biomeId;
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
