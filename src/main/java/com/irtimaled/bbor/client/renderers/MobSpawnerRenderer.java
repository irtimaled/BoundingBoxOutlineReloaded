package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class MobSpawnerRenderer extends AbstractRenderer<BoundingBoxMobSpawner> {
    @Override
    public void render(BoundingBoxMobSpawner boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = boundingBox.getColor();

        renderCuboid(new OffsetBox(coords, coords), color);

        if (ConfigManager.renderMobSpawnerActivationLines.get()) {
            renderActivationLine(new OffsetPoint(coords).offset(0.5, 0.5, 0.5));
        }

        if (ConfigManager.renderMobSpawnerSpawnArea.get()) {
            OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
            renderCuboid(bb, color);
        }
    }

    private void renderActivationLine(OffsetPoint centerPoint) {
        OffsetPoint playerPos = new OffsetPoint(Player.getX(), Player.getY(), Player.getZ());
        double distance = centerPoint.getDistance(playerPos);
        if (distance <= 20) {
            Color color = distance > 18 ? Color.RED : distance > 16 ? Colors.DARK_ORANGE : Color.GREEN;

            OffsetPoint playerPoint = playerPos.offset(0, 0.1, 0);
            renderLine(centerPoint, playerPoint, color);
        }
    }
}
