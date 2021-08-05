package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class SpawnableBlocksRenderer extends AbstractRenderer<BoundingBoxSpawnableBlocks> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxSpawnableBlocks boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        for (BlockPos c : boundingBox.getBlocks()) {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(matrixStack, offsetBox, color, false, 30);
        }
    }
}
