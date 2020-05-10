package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    private static final double PI = TAU / 2D;

    public abstract void render(T boundingBox);

    void renderCuboid(OffsetBox bb, Color color) {
        OffsetBox nudge = bb.nudge();
        if (ConfigManager.fill.get()) {
            renderFilledFaces(nudge.getMin(), nudge.getMax(), color, 30);
        }
        renderOutlinedCuboid(nudge, color);
    }

    void renderOutlinedCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderFaces(bb.getMin(), bb.getMax(), color, 255);
    }

    private void renderFaces(OffsetPoint min, OffsetPoint max, Color color, int alpha) {
        double minX = min.getX();
        double minY = min.getY();
        double minZ = min.getZ();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();

        if(ConfigManager.invertBoxColorPlayerInside.get() &&
                playerInsideBoundingBox(minX, minY, minZ, maxX, maxY, maxZ)) {
            color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }

        Renderer renderer = Renderer.startQuads()
                .setColor(color)
                .setAlpha(alpha);

        if(minX != maxX && minZ != maxZ) {
            renderer.addPoint(minX, minY, minZ)
                    .addPoint(maxX, minY, minZ)
                    .addPoint(maxX, minY, maxZ)
                    .addPoint(minX, minY, maxZ);

            if (minY != maxY) {
                renderer.addPoint(minX, maxY, minZ)
                        .addPoint(maxX, maxY, minZ)
                        .addPoint(maxX, maxY, maxZ)
                        .addPoint(minX, maxY, maxZ);
            }
        }

        if(minX != maxX && minY != maxY) {
            renderer.addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, minY, maxZ);

            if(minZ != maxZ) {
                renderer.addPoint(minX, minY, minZ)
                        .addPoint(minX, maxY, minZ)
                        .addPoint(maxX, maxY, minZ)
                        .addPoint(maxX, minY, minZ);
            }
        }
        if(minY != maxY && minZ != maxZ) {
            renderer.addPoint(minX, minY, minZ)
                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(minX, maxY, minZ);

            if(minX != maxX) {
                renderer.addPoint(maxX, minY, minZ)
                        .addPoint(maxX, minY, maxZ)
                        .addPoint(maxX, maxY, maxZ)
                        .addPoint(maxX, maxY, minZ);
            }
        }
        renderer.render();
    }

    private boolean playerInsideBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return minX < 0 && maxX > 0 && minY < 0 && maxY > 0 && minZ < 0 && maxZ > 0;
    }

    void renderLine(OffsetPoint startPoint, OffsetPoint endPoint, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Renderer.startLines()
                .setColor(color)
                .addPoint(startPoint)
                .addPoint(endPoint)
                .render();
    }

    void renderFilledFaces(OffsetPoint min, OffsetPoint max, Color color, int alpha) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderFaces(min, max, color, alpha);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    void renderText(OffsetPoint offsetPoint, String... texts) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        GL11.glPushMatrix();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glTranslated(offsetPoint.getX(), offsetPoint.getY() + 0.002D, offsetPoint.getZ());
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.0175F, -0.0175F, 0.0175F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        float top = -(fontRenderer.FONT_HEIGHT * texts.length) / 2f;
        for (String text : texts) {
            float left = fontRenderer.getStringWidth(text) / 2f;
            fontRenderer.drawString(text, -left, top, -1);
            top += fontRenderer.FONT_HEIGHT;
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    void renderSphere(OffsetPoint center, double radius, Color color, int density, int dotSize) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(dotSize);
        Renderer renderer = Renderer.startPoints()
                .setColor(color);
        buildPoints(center, radius, density)
                .forEach(renderer::addPoint);
        renderer.render();
    }

    private Set<OffsetPoint> buildPoints(OffsetPoint center, double radius, int density) {
        int segments = 24 + (density * 8);

        Set<OffsetPoint> points = new HashSet<>(segments * segments);

        double thetaSegment = PI / (double) segments;
        double phiSegment = TAU / (double) segments;

        for (double phi = 0.0D; phi < TAU; phi += phiSegment) {
            for (double theta = 0.0D; theta < PI; theta += thetaSegment) {
                double dx = radius * Math.sin(phi) * Math.cos(theta);
                double dz = radius * Math.sin(phi) * Math.sin(theta);
                double dy = radius * Math.cos(phi);

                points.add(center.offset(dx, dy, dz));
            }
        }
        return points;
    }
}
