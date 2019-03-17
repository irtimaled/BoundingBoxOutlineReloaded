package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;

import java.awt.*;

public class MobSpawnerRenderer extends Renderer<BoundingBoxMobSpawner> {
    @Override
    public void render(BoundingBoxMobSpawner boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = boundingBox.getColor();
        if (ConfigManager.renderMobSpawnerSpawnArea.get()) {
            renderBoundingBox(boundingBox);
        } else {
            renderCuboid(getAxisAlignedBB(coords, coords, true), color, fill());
        }

        if (!ConfigManager.renderMobSpawnerActivationLines.get()) return;

        renderActivationLines(coords);
    }

    private void renderActivationLines(Coords coords) {
        OffsetPoint centerPoint = new OffsetPoint(coords).add(0.5, 0.5, 0.5);
        OffsetPoint playerPos = new OffsetPoint(PlayerCoords.getX(), PlayerCoords.getY(), PlayerCoords.getZ());
        double distance = centerPoint.getDistance(playerPos);
        if (distance > 20.0) return;

        Color color = distance <= 18.0 ? distance <= 16.0 ? Color.GREEN : Colors.DARK_ORANGE : Color.RED;
        renderLine(centerPoint, playerPos.add(0, 0.1, 0), color);

    }
}
