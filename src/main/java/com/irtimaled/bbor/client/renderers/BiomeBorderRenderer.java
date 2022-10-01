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
        final OffsetPoint offsetPoint = new OffsetPoint(coords);

        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());
        boolean fillOnly = !ConfigManager.drawBiomeBorderOutline.get();

        if (boundingBox.renderNorth()) { // z - 1
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 0), offsetPoint.offset(1, -1, 0)), color, fillOnly, 30);
        }
        if (boundingBox.renderWest()) { // x - 1
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 0), offsetPoint.offset(0, -1, 1)), color, fillOnly, 30);
        }
        if (boundingBox.renderDown()) { // y - 1
            renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, -1, 0), offsetPoint.offset(1, -1, 1)), color, fillOnly, 30);
        }
        if (ConfigManager.renderOnlyCurrentBiome.get()) {
            if (boundingBox.renderSouth()) {
                renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 1), offsetPoint.offset(1, -1, 1)), color, fillOnly, 30);
            }
            if (boundingBox.renderEast()) {
                renderCuboid(ctx, new OffsetBox(offsetPoint.offset(1, 0, 0), offsetPoint.offset(1, -1, 1)), color, fillOnly, 30);
            }
            if (boundingBox.renderUp()) {
                renderCuboid(ctx, new OffsetBox(offsetPoint.offset(0, 0, 0), offsetPoint.offset(1, 0, 1)), color, fillOnly, 30);
            }
        }
    }

    private double getOffset(double value) {
        return value > 0 ? -0.001F : 0.001F;
    }
}
