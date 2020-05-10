package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(BoundingBoxSpawningSphere boundingBox) {
        OffsetPoint sphereCenter = new OffsetPoint(boundingBox.getPoint());

        OffsetBox offsetBox = new OffsetBox(sphereCenter, sphereCenter).grow(0.5, 0, 0.5);
        renderCuboid(offsetBox, Color.GREEN);

        Integer spawnableSpacesCount = boundingBox.getSpawnableSpacesCount();
        if (spawnableSpacesCount != null) {
            renderText(sphereCenter, I18n.format("bbor.renderer.spawningSphere.spawnable"),
                    spawnableSpacesCount == 0 ?
                            I18n.format("bbor.renderer.spawningSphere.none") :
                            String.format("%,d", spawnableSpacesCount));
        }

        renderSphere(sphereCenter, BoundingBoxSpawningSphere.SAFE_RADIUS, Color.GREEN, 5, 5);
        renderSphere(sphereCenter, BoundingBoxSpawningSphere.SPAWN_RADIUS, Color.RED, 5, 5);

        if (ConfigManager.renderAFKSpawnableBlocks.get() && boundingBox.isWithinSphere(Player.getPoint())) {
            renderSpawnableSpaces(boundingBox);
        }
    }

    private void renderSpawnableSpaces(BoundingBoxSpawningSphere boundingBox) {
        boundingBox.getBlocks().forEach(c -> {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(offsetBox, boundingBox.getColor());
        });
    }
}
