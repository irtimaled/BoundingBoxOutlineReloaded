package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.function.Supplier;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    public static final double PHI_SEGMENT = TAU / 90D;
    private static final double PI = TAU / 2D;
    public static final double THETA_SEGMENT = PHI_SEGMENT / 2D;

    public abstract void render(MatrixStack matrixStack, T boundingBox);

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
        renderFilledFaces(min, max, color, 30);
    }

    void renderFilledFaces(OffsetPoint min, OffsetPoint max, Color color, int alpha) {
        if (!ConfigManager.fill.get()) return;
        RenderQueue.deferRendering(() -> renderFaces(min, max, color, alpha, Renderer::startQuads));
    }

    void renderText(MatrixStack matrixStack, OffsetPoint offsetPoint, String... texts) {
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        RenderHelper.beforeRenderFont(matrixStack, offsetPoint);
        float top = -(fontRenderer.fontHeight * texts.length) / 2f;
        for (String text : texts) {
            float left = fontRenderer.getWidth(text) / 2f;
            fontRenderer.draw(new MatrixStack(), text, -left, top, -1);
            top += fontRenderer.fontHeight;
        }
        RenderHelper.afterRenderFont(matrixStack);
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
        int dyStep = radius < 64 ? 1 : MathHelper.floor(radius / 32);
        for (double dy = offset - radius; dy <= radius + 1; dy += dyStep) {
            double circleRadius = Math.sqrt((radius * radius) - (dy * dy));
            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
            renderCircle(center, circleRadius, color, dy + 0.001F);
        }
    }

    private void renderCircle(Point center, double radius, Color color, double dy) {
        Renderer renderer = Renderer.startLineLoop()
                .setColor(color);

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            renderer.addPoint(new OffsetPoint(center.offset(Math.cos(phi) * radius, dy, Math.sin(phi) * radius)));
        }

        renderer.render();
    }

    private void renderDotSphere(Point center, double radius, Color color) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Renderer renderer = Renderer.startQuads()
                .setColor(color);

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            double dy = radius * Math.cos(phi);
            double radiusBySinPhi = radius * Math.sin(phi);
            for (double theta = 0.0D; theta < PI; theta += THETA_SEGMENT) {
                double dx = radiusBySinPhi * Math.cos(theta);
                double dz = radiusBySinPhi * Math.sin(theta);

                renderer.addPoint(new OffsetPoint(center.offset(dx, dy, dz)));
            }
        }
        renderer.render();
    }
}
