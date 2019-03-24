package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    public abstract void render(T boundingBox);

    void renderBoundingBox(T boundingBox) {
        OffsetBox bb = new OffsetBox(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
        renderCuboid(bb, boundingBox.getColor(), fill());
    }

    boolean fill() {
        return ConfigManager.fill.get();
    }

    void renderLine(OffsetPoint point1, OffsetPoint point2, Color color) {
        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_LINES, worldRenderer.getVertexFormat());
        worldRenderer.pos(point1.getX(), point1.getY(), point1.getZ()).color(colorR, colorG, colorB, 255).endVertex();
        worldRenderer.pos(point2.getX(), point2.getY(), point2.getZ()).color(colorR, colorG, colorB, 255).endVertex();
        tessellator.draw();
    }

    void renderCuboid(OffsetBox bb, Color color, boolean fill) {
        bb = bb.nudge();
        if (fill) {
            renderFilledCuboid(bb, color);
        }
        renderUnfilledCuboid(bb, color);
    }

    private void renderFilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(bb, 30, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    private void renderUnfilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(bb, 255, color);
    }

    private void renderCuboid(OffsetBox bb, int alphaChannel, Color color) {
        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        OffsetPoint min = bb.getMin();
        OffsetPoint max = bb.getMax();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(min.getX(), min.getY(), min.getZ())
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(max.getX(), min.getY(), min.getZ())
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(max.getX(), min.getY(), max.getZ())
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(min.getX(), min.getY(), max.getZ())
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();

        if (min.getY() != max.getY()) {
            worldRenderer.pos(min.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(min.getX(), min.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), min.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(min.getX(), min.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), min.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(min.getX(), min.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), min.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(min.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(max.getX(), min.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), min.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), max.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(max.getX(), max.getY(), min.getZ())
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
        }
        tessellator.draw();
    }
}
