package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.models.BoundingBoxSlimeChunk;
import com.irtimaled.bbor.config.ConfigManager;

import java.awt.*;

public class SlimeChunkRenderer extends AbstractRenderer<BoundingBoxSlimeChunk> {
    @Override
    public void render(BoundingBoxSlimeChunk boundingBox) {
        OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
        Color color = boundingBox.getColor();
        renderCuboid(bb, color);

        double maxY = Player.getMaxY(ConfigManager.slimeChunkMaxY.get());
        double dY = maxY - 39;
        if (dY > 0) {
            OffsetPoint min = bb.getMin().offset(0, 38, 0);
            OffsetPoint max = bb.getMax().offset(0, dY, 0);
            renderCuboid(new OffsetBox(min, max), color);
        }
    }
}
