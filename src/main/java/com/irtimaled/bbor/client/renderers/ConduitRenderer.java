package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxConduit;
import com.irtimaled.bbor.common.models.Colors;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class ConduitRenderer extends AbstractRenderer<BoundingBoxConduit> {
    @Override
    public void render(BoundingBoxConduit boundingBox) {
        Coords center = boundingBox.getCenter();
        int level = boundingBox.getLevel();
        Color color = boundingBox.getColor();

        renderCuboid(new OffsetBox(center, center), color);
        if (level == 6 && ConfigManager.renderConduitMobHarmArea.get()) {
            renderCuboid(new OffsetBox(center, center).grow(8, 8, 8), Colors.DARK_ORANGE);
        }
        if (level != 0) {
            OffsetPoint sphereCenter = new OffsetPoint(center)
                    .offset(boundingBox.getCenterOffsetX(), boundingBox.getCenterOffsetY(), boundingBox.getCenterOffsetZ());

            renderSphere(sphereCenter, boundingBox.getRadius() + 0.5, color, 5, 5);
        }
    }
}
