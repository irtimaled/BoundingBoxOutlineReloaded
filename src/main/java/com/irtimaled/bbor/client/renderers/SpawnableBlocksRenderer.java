package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.awt.*;

public class SpawnableBlocksRenderer extends AbstractRenderer<BoundingBoxSpawnableBlocks> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxSpawnableBlocks boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        final IntIterator iterator = boundingBox.getBlockYs().iterator();
        while (iterator.hasNext()) {
            int y = iterator.nextInt();
            int x = boundingBox.getBaseX();
            int z = boundingBox.getBaseZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(ctx, offsetBox, color, false, 30);
        }
    }
}
