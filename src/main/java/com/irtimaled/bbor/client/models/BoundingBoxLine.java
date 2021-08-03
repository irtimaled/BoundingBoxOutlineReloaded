package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.LineRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;

public class BoundingBoxLine extends AbstractBoundingBox {
    private static final AbstractRenderer<BoundingBoxLine> RENDERER = ClientRenderer.registerRenderer(BoundingBoxLine.class, new LineRenderer());

    private final Point minPoint;
    private final Point maxPoint;
    private final Double width;
    private final Point[] corners;

    protected BoundingBoxLine(Point minPoint, Point maxPoint, double width, BoundingBoxType type, Point... corners) {
        super(type);
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.width = width;
        this.corners = corners;
    }

    public static BoundingBoxLine from(Point minPoint, Point maxPoint, Double width, BoundingBoxType type) {
        if (width == 0) return new BoundingBoxLine(minPoint, maxPoint, width, type);

        double halfWidth = width / 2.0d;

        double dx = maxPoint.getX() - minPoint.getX();
        double dz = maxPoint.getZ() - minPoint.getZ();

        double dxm = dx == 0 ? 0 : dx / Math.abs(dx);
        double dzm = dz == 0 ? 0 : dz / Math.abs(dz);

        double xc, zc;
        if (dxm == 0 || dzm == 0) {
            xc = Math.abs(dzm) * halfWidth;
            zc = Math.abs(dxm) * halfWidth;
        } else {
            double h = Math.sqrt(dx * dx + dz * dz);
            double theta = Math.acos((dz * dz + h * h - dx * dx) / (2 * dz * h));
            zc = halfWidth * Math.sin(theta);
            xc = Math.sqrt(halfWidth * halfWidth - zc * zc) * dxm * dzm;
        }

        return new BoundingBoxLine(minPoint, maxPoint, width, type,
                new Point(minPoint.getX() + xc, minPoint.getY(), minPoint.getZ() - zc),
                new Point(minPoint.getX() - xc, minPoint.getY(), minPoint.getZ() + zc),
                new Point(maxPoint.getX() - xc, maxPoint.getY(), maxPoint.getZ() + zc),
                new Point(maxPoint.getX() + xc, maxPoint.getY(), maxPoint.getZ() - zc));
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

    public double getWidth() {
        return width;
    }

    public Point[] getCorners() {
        return corners;
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
}
