package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSphere;
import com.irtimaled.bbor.client.models.Point;

public class SphereRenderer extends AbstractRenderer<BoundingBoxSphere> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxSphere boundingBox) {
        Point point = boundingBox.getPoint();
        double radius = boundingBox.getRadius();
        renderSphere(ctx, point, radius, BoundingBoxTypeHelper.getColor(boundingBox.getType()));
    }
}
