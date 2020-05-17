package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;

import java.awt.*;

public class SpawnableBlocksRenderer extends AbstractRenderer<BoundingBoxSpawnableBlocks> {
    @Override
    public void render(BoundingBoxSpawnableBlocks boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        boundingBox.getBlocks().forEach(c -> {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(offsetBox, color);
        });
    }
}
