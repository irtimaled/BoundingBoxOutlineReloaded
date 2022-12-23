package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.LineRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;

public class BoundingBoxLine extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxLine> RENDERER = CommonInterop.registerRenderer(BoundingBoxLine.class, () -> new LineRenderer());

    private final Point minPoint;
    private final Point maxPoint;

    protected BoundingBoxLine(Point minPoint, Point maxPoint, BoundingBoxType type) {
        super(type);
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    public static BoundingBoxLine from(Point minPoint, Point maxPoint, BoundingBoxType type) {
        return new BoundingBoxLine(minPoint, maxPoint, type);
    }

    @Override
    public int hashCode() {
        return TypeHelper.combineHashCodes(minPoint.hashCode(), maxPoint.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBoxLine other = (BoundingBoxLine) obj;
        return minPoint.equals(other.minPoint) && maxPoint.equals(other.maxPoint);
    }

    public Point getMinPoint() {
        return minPoint;
    }

    public Point getMaxPoint() {
        return maxPoint;
    }

    @Override
    public Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ) {
        boolean minXWithinBounds = isBetween(minPoint.getX(), minX, maxX);
        boolean maxXWithinBounds = isBetween(maxPoint.getX(), minX, maxX);
        boolean minZWithinBounds = isBetween(minPoint.getZ(), minZ, maxZ);
        boolean maxZWithinBounds = isBetween(maxPoint.getZ(), minZ, maxZ);

        return (minXWithinBounds && minZWithinBounds) ||
                (maxXWithinBounds && maxZWithinBounds) ||
                (minXWithinBounds && maxZWithinBounds) ||
                (maxXWithinBounds && minZWithinBounds);
    }

    @Override
    protected double getDistanceX(double x) {
        return x - MathHelper.clamp(x, minPoint.getX(), maxPoint.getX());
    }

    @Override
    protected double getDistanceY(double y) {
        return y - MathHelper.clamp(y, minPoint.getY(), maxPoint.getY());
    }

    @Override
    protected double getDistanceZ(double z) {
        return z - MathHelper.clamp(z, minPoint.getZ(), maxPoint.getZ());
    }

    private boolean isBetween(double val, int min, int max) {
        return val >= min && val <= max;
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }

    @Override
    public boolean isVisibleCulling() {
        return RenderCulling.isVisibleCulling(minPoint.getX(), minPoint.getY(), minPoint.getZ(), maxPoint.getX(), maxPoint.getY(), maxPoint.getZ()); // TODO better culling
    }
}
