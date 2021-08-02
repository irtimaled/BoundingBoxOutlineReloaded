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
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    public static final double PHI_SEGMENT = TAU / 90D;
    private static final double PI = TAU / 2D;
    public static final double THETA_SEGMENT = PHI_SEGMENT / 2D;
    private static final float DEFAULT_LINE_WIDTH = 0.0025f;

    private static final Box ORIGIN_BOX = new Box(BlockPos.ORIGIN);

    public abstract void render(MatrixStack matrixStack, T boundingBox);

    void renderCuboid(MatrixStack matrixStack, OffsetBox bb, Color color, boolean fillOnly, int fillAlpha) {
        OffsetBox nudge = bb.nudge();

        GL11.glEnable(GL11.GL_CULL_FACE);
        RenderHelper.polygonModeFill();
        matrixStack.push();

        renderCuboid0(matrixStack, nudge, color, fillOnly, fillAlpha, false);

        matrixStack.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1);
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
        RenderSystem.setShader(GameRenderer::getPositionShader);
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

//        Matrix4f viewMatrix = stack.peek().getModel();
//        Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
//        Shader shader = RenderSystem.getShader();
        if (fillOnly || ConfigManager.fill.get()) {
//            RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, fillAlpha / 255F);
            RenderBatch.drawSolidBox(stack.peek(), ORIGIN_BOX, color, fillAlpha, mask);
        }
        if (!fillOnly) {
//            outlinedBox.setShader(viewMatrix, projMatrix, shader);
            final double minXL = minX - getLineWidth();
            final double minYL = minY - getLineWidth();
            final double minZL = minZ - getLineWidth();
            final double maxXL = maxX + getLineWidth();
            final double maxYL = maxY + getLineWidth();
            final double maxZL = maxZ + getLineWidth();
            stack.push();
            stack.peek().getModel().load(lastStack.getModel());
            stack.peek().getNormal().load(lastStack.getNormal());
            renderLine(stack, new OffsetPoint(minXL, minYL, minZL), new OffsetPoint(maxXL, minYL, minZL), color);
            renderLine(stack, new OffsetPoint(maxXL, minYL, minZL), new OffsetPoint(maxXL, minYL, maxZL), color);
            renderLine(stack, new OffsetPoint(maxXL, minYL, maxZL), new OffsetPoint(minXL, minYL, maxZL), color);
            renderLine(stack, new OffsetPoint(minXL, minYL, maxZL), new OffsetPoint(minXL, minYL, minZL), color);
            renderLine(stack, new OffsetPoint(minXL, minYL, minZL), new OffsetPoint(minXL, maxYL, minZL), color);
            renderLine(stack, new OffsetPoint(maxXL, minYL, minZL), new OffsetPoint(maxXL, maxYL, minZL), color);
            renderLine(stack, new OffsetPoint(maxXL, minYL, maxZL), new OffsetPoint(maxXL, maxYL, maxZL), color);
            renderLine(stack, new OffsetPoint(minXL, minYL, maxZL), new OffsetPoint(minXL, maxYL, maxZL), color);
            renderLine(stack, new OffsetPoint(minXL, maxYL, minZL), new OffsetPoint(maxXL, maxYL, minZL), color);
            renderLine(stack, new OffsetPoint(maxXL, maxYL, minZL), new OffsetPoint(maxXL, maxYL, maxZL), color);
            renderLine(stack, new OffsetPoint(maxXL, maxYL, maxZL), new OffsetPoint(minXL, maxYL, maxZL), color);
            renderLine(stack, new OffsetPoint(minXL, maxYL, maxZL), new OffsetPoint(minXL, maxYL, minZL), color);
            stack.pop();
        }

        stack.pop();
    }

    private boolean playerInsideBoundingBox(OffsetBox nudge) {
        return nudge.getMin().getX() < 0 && nudge.getMax().getX() > 0 &&
                nudge.getMin().getY() < 0 && nudge.getMax().getY() > 0 &&
                nudge.getMin().getZ() < 0 && nudge.getMax().getZ() > 0;
    }

    private double getLineWidth() {
        return DEFAULT_LINE_WIDTH * ConfigManager.lineWidthModifier.get();
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
        RenderHelper.lineWidth2();

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
                renderCuboid0(matrixStack, new OffsetBox(point.offset(-getLineWidth(), -getLineWidth(), -getLineWidth()), point.offset(getLineWidth(), getLineWidth(), getLineWidth())), color, true, 255, true);
            }
        }
        matrixStack.pop();
    }
}
