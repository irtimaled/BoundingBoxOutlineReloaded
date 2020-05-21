package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(BoundingBoxSpawningSphere boundingBox) {
        Point point = boundingBox.getPoint();
        OffsetPoint sphereCenter = new OffsetPoint(point);

        OffsetBox offsetBox = new OffsetBox(sphereCenter, sphereCenter).grow(0.5, 0, 0.5);
        Color safeAreaColor = ColorHelper.getColor(ConfigManager.colorAFKSpheresSafeArea);
        renderCuboid(offsetBox, safeAreaColor);

        Integer spawnableSpacesCount = boundingBox.getSpawnableSpacesCount();
        if (spawnableSpacesCount != null) {
            renderText(sphereCenter, I18n.format("bbor.renderer.spawningSphere.spawnable"),
                    spawnableSpacesCount == 0 ?
                            I18n.format("bbor.renderer.spawningSphere.none") :
                            String.format("%,d", spawnableSpacesCount));
        }

        renderSphere(point, BoundingBoxSpawningSphere.SAFE_RADIUS, safeAreaColor, 5, 5);

        renderOuterSphere(boundingBox, point);

        if (ConfigManager.renderAFKSpawnableBlocks.get() && boundingBox.isWithinSphere(Player.getPoint())) {
            renderSpawnableSpaces(boundingBox);
        }
    }

    private void renderOuterSphere(BoundingBoxSpawningSphere boundingBox, Point point) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        renderSphere(point, BoundingBoxSpawningSphere.SPAWN_RADIUS, color, 5, 5);
    }

    private void renderSpawnableSpaces(BoundingBoxSpawningSphere boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(BoundingBoxType.SpawnableBlocks);
        boundingBox.getBlocks().forEach(c -> {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(offsetBox, color);
        });
    }
}
