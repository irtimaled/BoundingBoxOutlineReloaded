package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.SlimeChunkRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxSlimeChunk extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxSlimeChunk> RENDERER = ClientRenderer.registerRenderer(BoundingBoxSlimeChunk.class, () -> new SlimeChunkRenderer());

    public BoundingBoxSlimeChunk(Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.SlimeChunks);
    }

    @Override
    public double getDistanceY(double y) {
        double maxY = Player.getMaxY(ConfigManager.slimeChunkMaxY.get());
        if(maxY > 39)
            return  y - MathHelper.clamp(y, 0, maxY);
        else
            return super.getDistanceY(y);
    }

    @Override
    public AbstractRenderer<BoundingBoxSlimeChunk> getRenderer() {
        return RENDERER;
    }
}
