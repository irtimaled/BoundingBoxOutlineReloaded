package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerData;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public class WorldSpawnRenderer extends Renderer<BoundingBoxWorldSpawn> {
    @Override
    public void render(BoundingBoxWorldSpawn boundingBox) {
        AxisAlignedBB aaBB = boundingBox.toAxisAlignedBB(false);
        Color color = boundingBox.getColor();
        double y = PlayerData.getMaxY(ConfigManager.worldSpawnMaxY.get()) + 0.001F;
        renderRectangle(aaBB, y, y, color, false);
    }
}
