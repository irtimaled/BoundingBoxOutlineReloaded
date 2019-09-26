package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxSphere extends AbstractBoundingBox {
    private final Coords center;
    private final Integer radius;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;

    private Double centerOffsetX = 0d;
    private Double centerOffsetY = 0d;
    private Double centerOffsetZ = 0d;

    protected BoundingBoxSphere(BoundingBoxType type, Coords center, Integer radius) {
        super(type);
        this.center = center;
        this.radius = radius;

        this.minX = center.getX() - radius;
        this.minZ = center.getZ() - radius;
        this.maxX = center.getX() + radius;
        this.maxZ = center.getZ() + radius;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        return this.maxX >= minX &&
                this.maxZ >= minZ &&
                this.minX <= maxX &&
                this.minZ <= maxZ;
    }

    @Override
    public String toString() {
        return "(" + center.toString() + "; " + radius.toString() + ")";
    }

    public Integer getRadius() {
        return radius;
    }

    public Coords getCenter() {
        return center;
    }

    public Double getCenterOffsetX() {
        return centerOffsetX;
    }

    public Double getCenterOffsetY() {
        return centerOffsetY;
    }

    public Double getCenterOffsetZ() {
        return centerOffsetZ;
    }

    void setCenterOffsets(double x, double y, double z) {
        this.centerOffsetX = x;
        this.centerOffsetY = y;
        this.centerOffsetZ = z;
    }
}
