package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxStructure;

public class StructureRenderer extends AbstractRenderer<BoundingBoxStructure> {
    @Override
    public void render(BoundingBoxStructure boundingBox) {
        renderBoundingBox(boundingBox);
    }
}
