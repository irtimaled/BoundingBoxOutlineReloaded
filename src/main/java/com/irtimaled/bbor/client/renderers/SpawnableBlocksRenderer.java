package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;

public class SpawnableBlocksRenderer extends AbstractRenderer<BoundingBoxSpawnableBlocks> {
    @Override
    public void render(BoundingBoxSpawnableBlocks boundingBox) {
        boundingBox.getBlocks().forEach(c ->        {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(offsetBox, boundingBox.getColor());
        });
    }
}
