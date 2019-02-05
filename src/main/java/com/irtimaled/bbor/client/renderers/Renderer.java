package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.PlayerData;
import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class Renderer<T extends BoundingBox> {
    public abstract void render(T boundingBox);

    void renderBoundingBox(T boundingBox) {
        renderCuboid(boundingBox.toAxisAlignedBB(), boundingBox.getColor(), fill());
    }

    boolean fill() {
        return ConfigManager.fill.getBoolean();
    }

    void renderRectangle(AxisAlignedBB aaBB, double minY, double maxY, Color color, Boolean fill) {
        aaBB = new AxisAlignedBB(aaBB.minX, minY, aaBB.minZ, aaBB.maxX, maxY, aaBB.maxZ);
        renderCuboid(aaBB, color, fill);
    }

    void renderCuboid(AxisAlignedBB aaBB, Color color, boolean fill) {
        aaBB = offsetAxisAlignedBB(aaBB);
        if (fill) {
            renderFilledCuboid(aaBB, color);
        }
        renderUnfilledCuboid(aaBB, color);
    }

    private AxisAlignedBB offsetAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        double growXZ = 0.001F;
        double growY = 0;
        if (axisAlignedBB.minY != axisAlignedBB.maxY) {
            growY = growXZ;
        }
        return axisAlignedBB
                .grow(growXZ, growY, growXZ)
                .offset(-PlayerData.getX(), -PlayerData.getY(), -PlayerData.getZ());
    }

    private void renderFilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(aaBB, 30, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    private void renderUnfilledCuboid(AxisAlignedBB aaBB, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(aaBB, 255, color);
    }

    private void renderCuboid(AxisAlignedBB bb, int alphaChannel, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();
        worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                .color(colorR, colorG, colorB, alphaChannel)
                .endVertex();

        if (bb.minY != bb.maxY) {

            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.minX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.minX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();

            worldRenderer.pos(bb.maxX, bb.minY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.minY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.maxZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
            worldRenderer.pos(bb.maxX, bb.maxY, bb.minZ)
                    .color(colorR, colorG, colorB, alphaChannel)
                    .endVertex();
        }
        tessellator.draw();
    }
}
