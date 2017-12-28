package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxSlimeChunk;
import com.irtimaled.bbor.client.PlayerData;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public class SlimeChunkRenderer extends Renderer<BoundingBoxSlimeChunk> {
    @Override
    public void render(BoundingBoxSlimeChunk boundingBox) {
        AxisAlignedBB aaBB = boundingBox.toAxisAlignedBB();
        Color color = boundingBox.getColor();
        renderCuboid(aaBB, color, fill());

        double maxY = PlayerData.getMaxY(ConfigManager.slimeChunkMaxY.getInt());
        if (maxY > 39) {
            renderRectangle(aaBB, 39, maxY, color, fill());
        }
    }
}
