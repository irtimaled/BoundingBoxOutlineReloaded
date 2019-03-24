package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.config.ConfigManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    public abstract void render(T boundingBox);

    void renderBoundingBox(T boundingBox) {
        OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
        renderCuboid(bb, boundingBox.getColor());
    }


    void renderCuboid(OffsetBox bb, Color color) {
        Boolean fill = ConfigManager.fill.get();
        if (fill) {
            renderFilledCuboid(bb, color);
        }
        renderUnfilledCuboid(bb, color);
    }

    private void renderFilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(bb.nudge(), color, 30);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    void renderUnfilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(bb.nudge(), color, 255);
    }

    private void renderCuboid(OffsetBox box, Color color, int alpha) {
        OffsetPoint min = box.getMin();
        OffsetPoint max = box.getMax();

        double minX = min.getX();
        double minY = min.getY();
        double minZ = min.getZ();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();

        Renderer renderer = Renderer.startQuads()
                .setColor(color)
                .setAlpha(alpha)
                .addPoint(minX, minY, minZ)
                .addPoint(maxX, minY, minZ)
                .addPoint(maxX, minY, maxZ)
                .addPoint(minX, minY, maxZ);

        if (minY != maxY) {
            renderer.addPoint(minX, maxY, minZ)
                    .addPoint(maxX, maxY, minZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(minX, maxY, maxZ)

                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, minY, maxZ)

                    .addPoint(minX, minY, minZ)
                    .addPoint(minX, maxY, minZ)
                    .addPoint(maxX, maxY, minZ)
                    .addPoint(maxX, minY, minZ)

                    .addPoint(minX, minY, minZ)
                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(minX, maxY, minZ)

                    .addPoint(maxX, minY, minZ)
                    .addPoint(maxX, minY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, maxY, minZ);
        }
        renderer.render();

    }
}
