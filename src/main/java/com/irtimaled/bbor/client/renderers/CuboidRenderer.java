package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;

public class CuboidRenderer extends AbstractRenderer<BoundingBoxCuboid> {
    @Override
    public void render(BoundingBoxCuboid boundingBox) {
        OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
        renderCuboid(bb, BoundingBoxTypeHelper.getColor(boundingBox.getType()));
    }
}
