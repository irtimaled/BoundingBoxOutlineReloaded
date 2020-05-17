package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxLine;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

public class LineRenderer extends AbstractRenderer<BoundingBoxLine> {
    @Override
    public void render(BoundingBoxLine boundingBox) {
        Color color = BoundingBoxTypeHelper.getColor(boundingBox.getType());

        if (boundingBox.getWidth() == 0) {
            OffsetPoint startPoint = new OffsetPoint(boundingBox.getMinPoint()).offset(0, 0.001f, 0);
            OffsetPoint endPoint = new OffsetPoint(boundingBox.getMaxPoint()).offset(0, 0.001f, 0);
            renderLine(startPoint, endPoint, color);
            return;
        }

        OffsetPoint[] cornerPoints = Arrays.stream(boundingBox.getCorners()).
                map(point -> new OffsetPoint(point).offset(0,0.001f, 0)).
                toArray(OffsetPoint[]::new);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Renderer.startQuads()
                .setColor(color)
                .addPoints(cornerPoints)
                .render();

        if(!ConfigManager.fill.get()) return;

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        Renderer.startQuads()
                .setColor(color)
                .setAlpha(30)
                .addPoints(cornerPoints)
                .render();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }
}
