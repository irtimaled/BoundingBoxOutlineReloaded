package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxSlimeChunk extends BoundingBoxCuboid {
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
}
