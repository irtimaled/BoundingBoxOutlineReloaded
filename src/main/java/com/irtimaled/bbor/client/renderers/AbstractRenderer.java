package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.function.Supplier;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    private static final double PI = TAU / 2D;

    public abstract void render(T boundingBox);

    void renderCuboid(OffsetBox bb, Color color) {
        OffsetBox nudge = bb.nudge();
        renderOutlinedCuboid(nudge, color);
        renderFilledFaces(nudge.getMin(), nudge.getMax(), color);
    }

    void renderOutlinedCuboid(OffsetBox bb, Color color) {
        RenderHelper.polygonModeLine();
        OffsetPoint min = bb.getMin();
        OffsetPoint max = bb.getMax();
        renderFaces(min, max, color, 255, min.getY() == max.getY() ? Renderer::startLineLoop : Renderer::startLines);
    }

    private void renderFaces(OffsetPoint min, OffsetPoint max, Color color, int alpha, Supplier<Renderer> rendererSupplier) {
        double minX = min.getX();
        double minY = min.getY();
        double minZ = min.getZ();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();

        if (ConfigManager.invertBoxColorPlayerInside.get() &&
                playerInsideBoundingBox(minX, minY, minZ, maxX, maxY, maxZ)) {
            color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }

        Renderer renderer = rendererSupplier.get()
                .setColor(color)
                .setAlpha(alpha);

        if (minX != maxX && minZ != maxZ) {
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

        if (minX != maxX && minY != maxY) {
            renderer.addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, minY, maxZ);

            if (minZ != maxZ) {
                renderer.addPoint(minX, minY, minZ)
                        .addPoint(minX, maxY, minZ)
                        .addPoint(maxX, maxY, minZ)
                        .addPoint(maxX, minY, minZ);
            }
        }
        if (minY != maxY && minZ != maxZ) {
            renderer.addPoint(minX, minY, minZ)
                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(minX, maxY, minZ);

            if (minX != maxX) {
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
        RenderHelper.polygonModeLine();
        Renderer.startLines()
                .setColor(color)
                .addPoint(startPoint)
                .addPoint(endPoint)
                .render();
    }

    void renderFilledFaces(OffsetPoint min, OffsetPoint max, Color color) {
        if (!ConfigManager.fill.get()) return;

        RenderHelper.polygonModeFill();
        RenderHelper.enableBlend();
        renderFaces(min, max, color, 30, Renderer::startQuads);
        RenderHelper.disableBlend();
        RenderHelper.enablePolygonOffsetLine();
        RenderHelper.polygonOffsetMinusOne();
    }

    void renderText(OffsetPoint offsetPoint, String... texts) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        RenderHelper.beforeRenderFont(offsetPoint);
        float top = -(fontRenderer.FONT_HEIGHT * texts.length) / 2f;
        for (String text : texts) {
            float left = fontRenderer.getStringWidth(text) / 2f;
            fontRenderer.drawString(text, -left, top, -1);
            top += fontRenderer.FONT_HEIGHT;
        }
        RenderHelper.afterRenderFont();
    }

    void renderSphere(Point center, double radius, Color color) {
        if (ConfigManager.renderSphereAsDots.get()) {
            renderDotSphere(center, radius, color);
        } else {
            renderLineSphere(center, radius, color);
        }
    }

    private void renderLineSphere(Point center, double radius, Color color) {
        RenderHelper.lineWidth2();

        double offset = ((radius - (int) radius) == 0) ? center.getY() - (int) center.getY() : 0;
        for (double dy = offset - radius; dy <= radius + 1; dy++) {
            double circleRadius = Math.sqrt((radius * radius) - (dy * dy));
            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
            renderCircle(center, circleRadius, color, dy + 0.001F);
        }
    }

    private void renderCircle(Point center, double radius, Color color, double dy) {
        Renderer renderer = Renderer.startLineLoop()
                .setColor(color);

        for (int a = 0; a < 360; a += 5) {
            double heading = a * PI / 180;
            renderer.addPoint(new OffsetPoint(center.offset(Math.cos(heading) * radius, dy, Math.sin(heading) * radius)));
        }

        renderer.render();
    }

    private void renderDotSphere(Point center, double radius, Color color) {
        RenderHelper.enablePointSmooth();
        RenderHelper.pointSize5();
        Renderer renderer = Renderer.startPoints()
                .setColor(color);

        int segments = 64;
        double thetaSegment = PI / (double) segments;
        double phiSegment = TAU / (double) segments;

        for (double phi = 0.0D; phi < TAU; phi += phiSegment) {
            for (double theta = 0.0D; theta < PI; theta += thetaSegment) {
                double dx = radius * Math.sin(phi) * Math.cos(theta);
                double dz = radius * Math.sin(phi) * Math.sin(theta);
                double dy = radius * Math.cos(phi);

                renderer.addPoint(new OffsetPoint(center.offset(dx, dy, dz)));
            }
        }
        renderer.render();
    }
}
