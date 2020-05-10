package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;

public class BoundingBoxSphere extends AbstractBoundingBox {
    private final double radius;
    private final double minX;
    private final double minZ;
    private final double maxX;
    private final double maxZ;
    private final Point point;

    protected BoundingBoxSphere(Point point, double radius, BoundingBoxType type) {
        super(type);
        this.radius = radius;
        this.point = point;

        Coords center = point.getCoords();
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

    public double getRadius() {
        return radius;
    }

    public Point getPoint() {
        return point;
    }
}
