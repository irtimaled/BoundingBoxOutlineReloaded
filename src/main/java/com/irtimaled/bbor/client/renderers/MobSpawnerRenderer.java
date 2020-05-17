package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class MobSpawnerRenderer extends AbstractRenderer<BoundingBoxMobSpawner> {
    @Override
    public void render(BoundingBoxMobSpawner boundingBox) {
        Coords coords = boundingBox.getCoords();
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());

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
        OffsetPoint playerPos = new OffsetPoint(Player.getPoint());
        double distance = centerPoint.getDistance(playerPos);
        if (distance <= 20) {

            OffsetPoint playerPoint = playerPos.offset(0, 0.1, 0);
            renderLine(centerPoint, playerPoint, getColor(distance));
        }
    }

    private Color getColor(double distance) {
        if (distance > 18) return ColorHelper.getColor(ConfigManager.colorMobSpawnersLineFarAway);
        if (distance > 16) return ColorHelper.getColor(ConfigManager.colorMobSpawnersLineNearby);
        return ColorHelper.getColor(ConfigManager.colorMobSpawnersLineActive);
    }
}
