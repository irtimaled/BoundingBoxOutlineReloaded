package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SphereRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxSphere extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxSphere> RENDERER = CommonInterop.registerRenderer(BoundingBoxSphere.class, () -> new SphereRenderer());

    private final double radius;
    private final double minX;
    private final double minZ;
    private final double maxX;
    private final double maxZ;
    private final Point point;

    public BoundingBoxSphere(Point point, double radius, BoundingBoxType type) {
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

    @Override
    protected double getDistanceX(double x) {
        return x - point.getX();
    }

    @Override
    protected double getDistanceY(double y) {
        return y - point.getY();
    }

    @Override
    protected double getDistanceZ(double z) {
        return z - point.getZ();
    }

    public double getRadius() {
        return radius;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
