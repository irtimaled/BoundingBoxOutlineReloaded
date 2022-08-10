package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxBiomeBorder;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class BiomeBorderRenderer extends AbstractRenderer<BoundingBoxBiomeBorder> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxBiomeBorder boundingBox) {
        Coords coords = boundingBox.getCoords();
        OffsetPoint northWest = new OffsetPoint(coords).offset(0, 0.001F, 0);
        OffsetPoint northEast = northWest.offset(1, 0, 0);
        OffsetPoint southWest = northWest.offset(0, 0, 1);

        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        if (boundingBox.renderNorth()) {
            render(ctx, northWest, northEast, color);
        }
        if (boundingBox.renderWest()) {
            render(ctx, northWest, southWest, color);
        }
        if (ConfigManager.renderOnlyCurrentBiome.get()) {
            OffsetPoint southEast = southWest.offset(1, 0, 0);
            if (boundingBox.renderSouth()) {
                render(ctx, southWest, southEast, color);
            }
            if (boundingBox.renderEast()) {
                render(ctx, northEast, southEast, color);
            }
        }
    }

    private void render(RenderingContext ctx, OffsetPoint topCorner1, OffsetPoint topCorner2, Color color) {
        double xOffset = 0d;
        double zOffset = 0d;
        if (topCorner1.getX() == topCorner2.getX()) {
            xOffset = getOffset(topCorner1.getX());
        } else {
            zOffset = getOffset(topCorner1.getZ());
        }

        topCorner1 = topCorner1.offset(xOffset, 0, zOffset);
        topCorner2 = topCorner2.offset(xOffset, 0, zOffset);

        renderLine(ctx, topCorner1, topCorner2, color, false);
        OffsetPoint bottomCorner2 = topCorner2.offset(0, 1, 0);
        renderCuboid(ctx, new OffsetBox(topCorner1, bottomCorner2), color, true, 30);
        OffsetPoint bottomCorner1 = topCorner1.offset(0, 1, 0);
        renderLine(ctx, bottomCorner1, bottomCorner2, color, false);
    }

    private double getOffset(double value) {
        return value > 0 ? -0.001F : 0.001F;
    }
}
