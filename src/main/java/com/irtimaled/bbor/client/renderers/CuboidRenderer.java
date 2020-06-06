package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import net.minecraft.client.util.math.MatrixStack;

public class CuboidRenderer extends AbstractRenderer<BoundingBoxCuboid> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxCuboid boundingBox) {
        OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
        renderCuboid(bb, BoundingBoxTypeHelper.getColor(boundingBox.getType()));
    }
}
