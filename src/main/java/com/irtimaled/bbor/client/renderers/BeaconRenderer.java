package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class BeaconRenderer extends AbstractRenderer<BoundingBoxBeacon> {
    @Override
    public void render(BoundingBoxBeacon boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = boundingBox.getColor();
        renderCuboid(new OffsetBox(coords, coords), color);
        if (boundingBox.getLevel() != 0) {
            renderCuboid(new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords()), color);
        }
    }
}
