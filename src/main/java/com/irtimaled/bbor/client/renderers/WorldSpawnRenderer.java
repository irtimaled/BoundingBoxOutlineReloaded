package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;

import java.awt.*;

public class WorldSpawnRenderer extends AbstractRenderer<BoundingBoxWorldSpawn> {
    @Override
    public void render(BoundingBoxWorldSpawn boundingBox) {
        Color color = boundingBox.getColor();
        Coords minCoords = boundingBox.getMinCoords();
        Coords maxCoords = boundingBox.getMaxCoords();

        double y = PlayerCoords.getMaxY(ConfigManager.worldSpawnMaxY.get());

        OffsetBox offsetBox = new OffsetBox(minCoords.getX(), y, minCoords.getZ(), maxCoords.getX(), y, maxCoords.getZ());
        renderUnfilledCuboid(offsetBox, color);
    }
}
