package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.config.ConfigManager;

import net.minecraft.client.Minecraft;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(BoundingBoxSpawningSphere boundingBox) {
        OffsetPoint sphereCenter = new OffsetPoint(boundingBox.getCenter())
                .offset(boundingBox.getCenterOffsetX(), boundingBox.getCenterOffsetY(), boundingBox.getCenterOffsetZ());

        OffsetBox offsetBox = new OffsetBox(sphereCenter, sphereCenter).grow(0.5, 0, 0.5);
        renderCuboid(offsetBox, Color.GREEN);

        Integer spawnableSpacesCount = boundingBox.getSpawnableSpacesCount();
        if (spawnableSpacesCount != null) {
            renderText(sphereCenter, "Spawnable", spawnableSpacesCount == 0 ? "None" : String.format("%,d", (int) spawnableSpacesCount));
        }

        renderSphere(sphereCenter, BoundingBoxSpawningSphere.SAFE_RADIUS, Color.GREEN, 5, 5);
        renderSphere(sphereCenter, BoundingBoxSpawningSphere.SPAWN_RADIUS, Color.RED, 5, 5);

        if(ConfigManager.renderAFKSpawnableBlocks.get()) {
            renderSpawnableSpaces(sphereCenter);
        }
    }

    private void renderSpawnableSpaces(OffsetPoint center) {
        Integer renderDistance = ConfigManager.afkSpawnableBlocksRenderDistance.get();
        int width = MathHelper.floor(Math.pow(2, 2 + renderDistance) * 3);
        int height = MathHelper.floor(Math.pow(2, renderDistance) * 3);
        
        // System.out.println(String.format("Rotation: %1f %2f @ %3f", Player.getYaw(), Player.getPitch(), Minecraft.getInstance().gameSettings.fov * 0.6));
        
        // long timeStartNormal = java.lang.System.currentTimeMillis();
        // SpawningSphereHelper.findSpawnableSpaces(center.getPoint(), Player.getCoords(), width, height, (x, y, z) -> true);
        // System.out.println(String.format("Normal render in : %1d ms", Math.round(java.lang.System.currentTimeMillis() - timeStartNormal)));

        // long timeStartFov = java.lang.System.currentTimeMillis();
        // SpawningSphereHelper.findSpawnableSpacesWithFov(center.getPoint(), Player.getCoords(), width, height, Player.getYaw(), Player.getPitch(), Minecraft.getInstance().gameSettings.fov, (x, y, z) -> true);
        // System.out.println(String.format("Fov render in : %1d ms", Math.round(java.lang.System.currentTimeMillis() - timeStartFov)));
        
        SpawningSphereHelper.findSpawnableSpacesWithFov(center.getPoint(), Player.getCoords(), width, height, Player.getYaw(), Player.getPitch(), Minecraft.getInstance().gameSettings.fov,
                (x, y, z) -> {
                    OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
                    renderCuboid(offsetBox, Color.RED);
                    return false;
                });
    }
}
