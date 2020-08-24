package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.models.BoundingBoxFlowerForest;
import com.irtimaled.bbor.common.models.Coords;

public class FlowerForestRenderer extends AbstractRenderer<BoundingBoxFlowerForest> {
    @Override
    public void render(BoundingBoxFlowerForest boundingBox) {
        Coords coords = boundingBox.getCoords();
        int x = coords.getX();
        int y = coords.getY();
        int z = coords.getZ();
        renderFilledFaces(new OffsetPoint(x, y + 0.01d, z),
                new OffsetPoint(x + 1, y + 0.01d, z + 1),
                boundingBox.getColor(), 127);
    }
}
