package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxLine;

import java.awt.*;

public class LineRenderer extends AbstractRenderer<BoundingBoxLine> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxLine boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());

        OffsetPoint startPoint = new OffsetPoint(boundingBox.getMinPoint()).offset(0, 0.001f, 0);
        OffsetPoint endPoint = new OffsetPoint(boundingBox.getMaxPoint()).offset(0, 0.001f, 0);
        renderLine(ctx, startPoint, endPoint, color, false);
    }
}
