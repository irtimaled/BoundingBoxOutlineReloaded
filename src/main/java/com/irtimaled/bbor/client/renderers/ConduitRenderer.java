package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxConduit;
import com.irtimaled.bbor.client.models.Point;

import java.awt.*;

public class ConduitRenderer extends AbstractRenderer<BoundingBoxConduit> {
    @Override
    public void render(BoundingBoxConduit boundingBox) {
        int level = boundingBox.getLevel();
        Point point = boundingBox.getPoint();
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());

        OffsetPoint center = new OffsetPoint(point);
        OffsetBox centerBox = new OffsetBox(center, center).grow(0.5, 0.5, 0.5);
        renderCuboid(centerBox, color);
        if (level == 6 && ConfigManager.renderConduitMobHarmArea.get()) {
            renderCuboid(centerBox.grow(8, 8, 8),
                    ColorHelper.getColor(ConfigManager.colorConduitMobHarmArea));
        }
        if (level != 0) {
            renderSphere(point, boundingBox.getRadius() + 0.5, color, 5, 5);
        }
    }
}
