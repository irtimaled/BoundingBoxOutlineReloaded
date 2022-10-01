package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBiomeBorder;
import com.irtimaled.bbor.common.models.Coords;

import java.awt.*;

public class BiomeBorderRenderer extends AbstractRenderer<BoundingBoxBiomeBorder> {
    @Override
    public void render(RenderingContext ctx, BoundingBoxBiomeBorder boundingBox) {
        Coords coords = boundingBox.getCoords();
        final OffsetPoint offsetPoint = new OffsetPoint(coords);

        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        if (boundingBox.renderNorth()) { // z - 1
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 0), offsetPoint.offset(1, -1, 0)), color, false, 30);
        }
        if (boundingBox.renderWest()) { // x - 1
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 0), offsetPoint.offset(0, -1, 1)), color, false, 30);
        }
        if (boundingBox.renderDown()) {
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, -1, 0), offsetPoint.offset(1, -1, 1)), color, false, 30);
        }
//        if (ConfigManager.renderOnlyCurrentBiome.get()) {
//            OffsetPoint southEast = southWest.offset(1, 0, 0);
//            if (boundingBox.renderSouth()) {
//                render(ctx, southWest, southEast, color);
//            }
//            if (boundingBox.renderEast()) {
//                render(ctx, northEast, southEast, color);
//            }
//        }
    }

    private double getOffset(double value) {
        return value > 0 ? -0.001F : 0.001F;
    }
}
