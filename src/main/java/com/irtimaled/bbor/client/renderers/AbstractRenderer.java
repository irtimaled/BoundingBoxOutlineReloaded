package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    public static final double PHI_SEGMENT = TAU / 90D;
    private static final double PI = TAU / 2D;
    public static final double THETA_SEGMENT = PHI_SEGMENT / 2D;

    private static final Box ORIGIN_BOX = new Box(BlockPos.ORIGIN);

    public abstract void render(MatrixStack matrixStack, T boundingBox);

    void renderCuboid(MatrixStack matrixStack, OffsetBox bb, Color color, boolean fillOnly, int fillAlpha) {
        OffsetBox nudge = bb.nudge();
        matrixStack.push();

        renderCuboid0(matrixStack, nudge, color, fillOnly, fillAlpha, false);

        matrixStack.pop();
    }

    private void renderCuboid0(MatrixStack stack, OffsetBox nudge, Color color, boolean fillOnly, int fillAlpha, boolean mask) {
        if (!RenderCulling.isVisibleCulling(nudge.toBox())) return;
        if (ConfigManager.invertBoxColorPlayerInside.get() &&
                playerInsideBoundingBox(nudge)) {
            color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }
        final MatrixStack.Entry lastStack = stack.peek();
        stack.push();
        int regionX = (((int) Camera.getX()) >> 9) << 9;
        int regionZ = (((int) Camera.getZ()) >> 9) << 9;
        RenderHelper.applyRegionalRenderOffset(stack);
        final double minX = nudge.getMin().getX();
        final double minY = nudge.getMin().getY();
        final double minZ = nudge.getMin().getZ();
        final double maxX = nudge.getMax().getX();
        final double maxY = nudge.getMax().getY();
        final double maxZ = nudge.getMax().getZ();
        stack.translate(minX - regionX, minY, minZ - regionZ);
        stack.scale((float) (maxX - minX),
                (float) (maxY - minY),
                (float) (maxZ - minZ));

        if (fillOnly || ConfigManager.fill.get()) {
            RenderBatch.drawSolidBox(stack.peek(), ORIGIN_BOX, color, fillAlpha, mask);
        }
        if (!fillOnly) {
            stack.push();
            stack.peek().getModel().load(lastStack.getModel());
            stack.peek().getNormal().load(lastStack.getNormal());
            renderLine(stack, new OffsetPoint(minX, minY, minZ), new OffsetPoint(maxX, minY, minZ), color);
            renderLine(stack, new OffsetPoint(maxX, minY, minZ), new OffsetPoint(maxX, minY, maxZ), color);
            renderLine(stack, new OffsetPoint(maxX, minY, maxZ), new OffsetPoint(minX, minY, maxZ), color);
            renderLine(stack, new OffsetPoint(minX, minY, maxZ), new OffsetPoint(minX, minY, minZ), color);
            renderLine(stack, new OffsetPoint(minX, minY, minZ), new OffsetPoint(minX, maxY, minZ), color);
            renderLine(stack, new OffsetPoint(maxX, minY, minZ), new OffsetPoint(maxX, maxY, minZ), color);
            renderLine(stack, new OffsetPoint(maxX, minY, maxZ), new OffsetPoint(maxX, maxY, maxZ), color);
            renderLine(stack, new OffsetPoint(minX, minY, maxZ), new OffsetPoint(minX, maxY, maxZ), color);
            renderLine(stack, new OffsetPoint(minX, maxY, minZ), new OffsetPoint(maxX, maxY, minZ), color);
            renderLine(stack, new OffsetPoint(maxX, maxY, minZ), new OffsetPoint(maxX, maxY, maxZ), color);
            renderLine(stack, new OffsetPoint(maxX, maxY, maxZ), new OffsetPoint(minX, maxY, maxZ), color);
            renderLine(stack, new OffsetPoint(minX, maxY, maxZ), new OffsetPoint(minX, maxY, minZ), color);
            stack.pop();
        }

        stack.pop();
    }

    private boolean playerInsideBoundingBox(OffsetBox nudge) {
        return nudge.getMin().getX() < 0 && nudge.getMax().getX() > 0 &&
                nudge.getMin().getY() < 0 && nudge.getMax().getY() > 0 &&
                nudge.getMin().getZ() < 0 && nudge.getMax().getZ() > 0;
    }


    void renderLine(MatrixStack matrixStack, OffsetPoint startPoint, OffsetPoint endPoint, Color color) {
//        if ((startPoint.getY() == endPoint.getY() && startPoint.getZ() == endPoint.getZ()) ||
//                (startPoint.getX() == endPoint.getX() && startPoint.getZ() == endPoint.getZ()) ||
//                (startPoint.getX() == endPoint.getX() && startPoint.getY() == endPoint.getY())) {
//            renderCuboid0(matrixStack, new OffsetBox(startPoint.offset(-getLineWidth(), -getLineWidth(), -getLineWidth()), endPoint.offset(getLineWidth(), getLineWidth(), getLineWidth())), color, true, 255, true);
//            return;
//        }

        if (!RenderCulling.isVisibleCulling(new OffsetBox(startPoint, endPoint).toBox())) return; // TODO better culling

        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);

        RenderBatch.drawLine(matrixStack.peek(), startPoint.getPoint(), endPoint.getPoint(), color, 255);

        matrixStack.pop();
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

    void renderSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (ConfigManager.renderSphereAsDots.get()) {
            renderDotSphere(matrixStack, center, radius, color);
        } else {
            renderLineSphere(matrixStack, center, radius, color);
        }
    }

    private void renderLineSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (!RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius))) return;

        double offset = ((radius - (int) radius) == 0) ? center.getY() - (int) center.getY() : 0;
        int dyStep = radius < 64 ? 1 : MathHelper.floor(radius / 32);
        for (double dy = offset - radius; dy <= radius + 1; dy += dyStep) {
            double circleRadius = Math.sqrt((radius * radius) - (dy * dy));
            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
            renderCircle(matrixStack, center, circleRadius, color, dy + 0.001F);
        }
    }

    private void renderCircle(MatrixStack matrixStack, Point center, double radius, Color color, double dy) {
        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        Point firstPoint = null;
        Point lastPoint = null;

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            final Point point = center.offset(Math.cos(phi) * radius, dy, Math.sin(phi) * radius);
            if (firstPoint == null) firstPoint = point;
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }
            RenderBatch.drawLine(matrixStack.peek(), lastPoint, point, color, 255);
        }
        RenderBatch.drawLine(matrixStack.peek(), lastPoint, firstPoint, color, 255);

        matrixStack.pop();
    }

    private void renderDotSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (!RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius))) return;
        matrixStack.push();

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            double dy = radius * Math.cos(phi);
            double radiusBySinPhi = radius * Math.sin(phi);
            for (double theta = 0.0D; theta < PI; theta += THETA_SEGMENT) {
                double dx = radiusBySinPhi * Math.cos(theta);
                double dz = radiusBySinPhi * Math.sin(theta);
                final Point point = center.offset(dx, dy, dz);
                renderCuboid0(matrixStack, new OffsetBox(point.offset(-0.0025f, -0.0025f, -0.0025f), point.offset(0.0025f, 0.0025f, 0.0025f)), color, true, 255, true);
            }
        }
        matrixStack.pop();
    }
}
