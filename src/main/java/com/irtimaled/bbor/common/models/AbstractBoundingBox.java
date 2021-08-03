package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;

public abstract class AbstractBoundingBox {
    private final BoundingBoxType type;

    protected AbstractBoundingBox(BoundingBoxType type) {
        this.type = type;
    }

    public abstract Boolean intersectsBounds(int minX, int minZ, int maxX, int maxZ);

    public BoundingBoxType getType() {
        return type;
    }

    public double getDistance(double x, double y, double z) {
        double dX = getDistanceX(x);
        double dY = getDistanceY(y);
        double dZ = getDistanceZ(z);
        return dX * dX + dY * dY + dZ * dZ;
    }

    protected abstract double getDistanceX(double x);

    protected abstract double getDistanceY(double y);

    protected abstract double getDistanceZ(double z);

    public AbstractRenderer<?> getRenderer() {
        return ClientRenderer.getRenderer(this.getClass());
    }
}
