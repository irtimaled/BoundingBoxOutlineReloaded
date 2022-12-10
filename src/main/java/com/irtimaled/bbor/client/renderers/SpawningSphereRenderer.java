package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class SpawningSphereRenderer extends AbstractRenderer<BoundingBoxSpawningSphere> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxSpawningSphere boundingBox) {
        Point point = boundingBox.getPoint();
        OffsetPoint sphereCenter = new OffsetPoint(point);

        Color safeAreaColor = ColorHelper.getColor(ConfigManager.colorAFKSpheresSafeArea);
        renderSphere(ctx, point, BoundingBoxSpawningSphere.SAFE_RADIUS, safeAreaColor);

        renderOuterSphere(ctx, boundingBox, point);

        OffsetBox offsetBox = new OffsetBox(sphereCenter, sphereCenter).grow(0.5, 0, 0.5);
        renderCuboid(ctx, offsetBox, safeAreaColor, false, 30);

        Integer spawnableSpacesCount = boundingBox.getSpawnableSpacesCount();
//        if (spawnableSpacesCount != null) {
//            renderText(ctx, sphereCenter, I18n.translate("bbor.renderer.spawningSphere.spawnable"),
//                    spawnableSpacesCount == 0 ?
//                            I18n.translate("bbor.renderer.spawningSphere.none") :
//                            String.format("%,d", spawnableSpacesCount));
//        }
        renderSphere(ctx, point, BoundingBoxSpawningSphere.SAFE_RADIUS, safeAreaColor);

        if (ConfigManager.renderAFKSpawnableBlocks.get() && boundingBox.isWithinSphere(Player.getPoint())) {
            renderSpawnableSpaces(ctx, boundingBox);
        }
    }

    private void renderOuterSphere(RenderingContext ctx, BoundingBoxSpawningSphere boundingBox, Point point) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        renderSphere(ctx, point, BoundingBoxSpawningSphere.SPAWN_RADIUS, color);
    }

    private void renderSpawnableSpaces(RenderingContext ctx, BoundingBoxSpawningSphere boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(BoundingBoxType.SpawnableBlocks);
        for (BlockPos c : boundingBox.getBlocksAllTime()) {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(ctx, offsetBox, color, false, 60);
        }
        for (BlockPos c : boundingBox.getBlocksNightOnly()) {
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            OffsetBox offsetBox = new OffsetBox(x, y, z, x + 1, y, z + 1);
            renderCuboid(ctx, offsetBox, color, false, 30);
        }
    }
}
