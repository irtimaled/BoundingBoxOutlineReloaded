package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxLine;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.Arrays;

public class LineRenderer extends AbstractRenderer<BoundingBoxLine> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxLine boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());

        if (boundingBox.getWidth() == 0) {
            OffsetPoint startPoint = new OffsetPoint(boundingBox.getMinPoint()).offset(0, 0.001f, 0);
            OffsetPoint endPoint = new OffsetPoint(boundingBox.getMaxPoint()).offset(0, 0.001f, 0);
            renderLine(matrixStack, startPoint, endPoint, color);
            return;
        }

        OffsetPoint[] cornerPoints = Arrays.stream(boundingBox.getCorners()).
                map(point -> new OffsetPoint(point).offset(0, 0.001f, 0)).
                toArray(OffsetPoint[]::new);

        RenderHelper.polygonModeLine();
        Renderer.startQuads()
                .setMatrixStack(matrixStack)
                .setColor(color)
                .addPoints(cornerPoints)
                .render();

        if (!ConfigManager.fill.get()) return;
        RenderQueue.deferRendering(() -> Renderer.startQuads()
                .setMatrixStack(matrixStack)
                .setColor(color)
                .setAlpha(30)
                .addPoints(cornerPoints)
                .render());
    }
}
