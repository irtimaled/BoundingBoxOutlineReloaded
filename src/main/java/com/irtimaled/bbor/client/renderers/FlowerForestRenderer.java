package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.models.BoundingBoxFlowerForest;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.util.math.MatrixStack;

public class FlowerForestRenderer extends AbstractRenderer<BoundingBoxFlowerForest> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxFlowerForest boundingBox) {
        Coords coords = boundingBox.getCoords();
        int x = coords.getX();
        int y = coords.getY();
        int z = coords.getZ();
        renderCuboid(matrixStack, new OffsetBox(new OffsetPoint(x, y + 0.01d, z),
                        new OffsetPoint(x + 1, y + 0.01d, z + 1)),
                boundingBox.getColor()/*, 127 alpha*/);
    }
}
