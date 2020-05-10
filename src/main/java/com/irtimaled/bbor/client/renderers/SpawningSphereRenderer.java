package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Point;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(BoundingBoxSpawningSphere boundingBox) {
        Point point = boundingBox.getPoint();
        OffsetPoint sphereCenter = new OffsetPoint(point);

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

        if(ConfigManager.renderAFKSpawnableBlocks.get()) {
            renderSpawnableSpaces(point);
        }
    }

    private void renderSpawnableSpaces(Point center) {
        Integer renderDistance = ConfigManager.afkSpawnableBlocksRenderDistance.get();
        int width = MathHelper.floor(Math.pow(2, 2 + renderDistance));
        int height = MathHelper.floor(Math.pow(2, renderDistance));

        SpawningSphereHelper.findSpawnableSpaces(center, Player.getCoords(), width, height,
                (x, y, z) -> {
                    OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
                    renderCuboid(offsetBox, Color.RED);
                    return false;
                });
    }
}
