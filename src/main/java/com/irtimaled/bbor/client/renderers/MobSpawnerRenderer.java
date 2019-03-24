package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;

import java.awt.*;

public class MobSpawnerRenderer extends AbstractRenderer<BoundingBoxMobSpawner> {
    @Override
    public void render(BoundingBoxMobSpawner boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = boundingBox.getColor();
        if (ConfigManager.renderMobSpawnerSpawnArea.get()) {
            renderBoundingBox(boundingBox);
        } else {
            renderCuboid(new OffsetBox(coords, coords), color, fill());
        }

        if (!ConfigManager.renderMobSpawnerActivationLines.get()) return;

        renderActivationLine(new OffsetPoint(coords).offset(0.5, 0.5, 0.5));
    }

    private void renderActivationLine(OffsetPoint centerPoint) {
        OffsetPoint playerPos = new OffsetPoint(PlayerCoords.getX(), PlayerCoords.getY(), PlayerCoords.getZ());
        double distance = centerPoint.getDistance(playerPos);
        if (distance <= 20) {
            Color color = distance > 18 ? Color.RED : distance > 16 ? Colors.DARK_ORANGE : Color.GREEN;
            renderLine(centerPoint, playerPos.offset(0, 0.1, 0), color);
        }
    }
}
