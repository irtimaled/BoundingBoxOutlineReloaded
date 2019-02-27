package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerData;
import com.irtimaled.bbor.common.models.BoundingBoxMobSpawner;
import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class MobSpawnerRenderer extends Renderer<BoundingBoxMobSpawner> {
    @Override
    public void render(BoundingBoxMobSpawner boundingBox) {
        BlockPos center = boundingBox.getCenter();
        Color color = boundingBox.getColor();
        OffsetPoint centerPoint = new OffsetPoint(center)
                .add(0.5, 0.5, 0.5);
        double radius = boundingBox.getRadius();
        if (ConfigManager.renderMobSpawnerSpawnArea.getBoolean()) {
            renderBoundingBox(boundingBox);
        } else {
            renderCuboid(new AxisAlignedBB(center, center).expand(1, 1, 1), color, fill());
        }

        if (!ConfigManager.renderMobSpawnerActivationLines.getBoolean()) return;

        OffsetPoint playerPos = new OffsetPoint(PlayerData.getX(), PlayerData.getY(), PlayerData.getZ());
        double distance = centerPoint.getDistance(playerPos);
        if (distance <= (radius * 1.25)) {
            if (distance > radius) {
                color = Colors.DARK_ORANGE;
            }
            if (distance > radius * 1.125) {
                color = Color.RED;
            }

            renderLine(centerPoint, playerPos.add(0, 0.1, 0), color);
        }
    }
}
