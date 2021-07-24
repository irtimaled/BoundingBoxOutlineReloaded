package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxSpawningSphere boundingBox) {
        Point point = boundingBox.getPoint();
        OffsetPoint sphereCenter = new OffsetPoint(point);

        Color safeAreaColor = ColorHelper.getColor(ConfigManager.colorAFKSpheresSafeArea);
        renderSphere(matrixStack, point, BoundingBoxSpawningSphere.SAFE_RADIUS, safeAreaColor);

        renderOuterSphere(matrixStack, boundingBox, point);

        OffsetBox offsetBox = new OffsetBox(sphereCenter, sphereCenter).grow(0.5, 0, 0.5);
        renderCuboid(matrixStack, offsetBox, safeAreaColor);

        Integer spawnableSpacesCount = boundingBox.getSpawnableSpacesCount();
        if (spawnableSpacesCount != null) {
            renderText(matrixStack, sphereCenter, I18n.translate("bbor.renderer.spawningSphere.spawnable"),
                    spawnableSpacesCount == 0 ?
                            I18n.translate("bbor.renderer.spawningSphere.none") :
                            String.format("%,d", spawnableSpacesCount));
        }
        renderSphere(matrixStack, point, BoundingBoxSpawningSphere.SAFE_RADIUS, safeAreaColor);

        if (ConfigManager.renderAFKSpawnableBlocks.get() && boundingBox.isWithinSphere(Player.getPoint())) {
            renderSpawnableSpaces(matrixStack, boundingBox);
        }
    }

    private void renderOuterSphere(MatrixStack matrixStack, BoundingBoxSpawningSphere boundingBox, Point point) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        renderSphere(matrixStack, point, BoundingBoxSpawningSphere.SPAWN_RADIUS, color);
    }

    private void renderSpawnableSpaces(MatrixStack matrixStack, BoundingBoxSpawningSphere boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(BoundingBoxType.SpawnableBlocks);
        boundingBox.getBlocks().forEach(c -> {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(matrixStack, offsetBox, color);
        });
    }
}
