package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class BeaconRenderer extends AbstractRenderer<BoundingBoxBeacon> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxBeacon boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        renderCuboid(ctx, new OffsetBox(coords, coords), color, false, 30);
        if (boundingBox.getLevel() != 0) {
            renderCuboid(ctx, new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords()), color, false, 30);
        }
    }
}
