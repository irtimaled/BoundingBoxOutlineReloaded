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
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static final double TAU = 6.283185307179586D;
    public static final double PHI_SEGMENT = TAU / 90D;
    private static final double PI = TAU / 2D;
    public static final double THETA_SEGMENT = PHI_SEGMENT / 2D;

    private final VertexBuffer solidBox = new VertexBuffer();
    private final VertexBuffer outlinedBox = new VertexBuffer();

    {
        final Box box = new Box(BlockPos.ORIGIN);
        RenderHelper.drawSolidBox(box, solidBox);
        RenderHelper.drawOutlinedBox(box, outlinedBox);
    }

    public abstract void render(MatrixStack matrixStack, T boundingBox);

    void renderCuboid(MatrixStack matrixStack, OffsetBox bb, Color color, boolean fillOnly) {
        OffsetBox nudge = bb.nudge();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
        RenderHelper.polygonModeFill();
        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);
        renderCuboid0(matrixStack, nudge, color, fillOnly);

        matrixStack.pop();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void renderCuboid0(MatrixStack stack, OffsetBox nudge, Color color, boolean fillOnly) {
        if (!RenderCulling.isVisibleCulling(nudge.toBox())) return;
        if (ConfigManager.invertBoxColorPlayerInside.get() &&
                playerInsideBoundingBox(nudge)) {
            color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }
        stack.push();
        int regionX = (((int) Camera.getX()) >> 9) << 9;
        int regionZ = (((int) Camera.getZ()) >> 9) << 9;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        stack.translate(nudge.getMin().getX() - regionX, nudge.getMin().getY(), nudge.getMin().getZ() - regionZ);
        stack.scale((float) (nudge.getMax().getX() - nudge.getMin().getX()),
                (float) (nudge.getMax().getY() - nudge.getMin().getY()),
                (float) (nudge.getMax().getZ() - nudge.getMin().getZ()));

        Matrix4f viewMatrix = stack.peek().getModel();
        Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
        Shader shader = RenderSystem.getShader();
        if (fillOnly || ConfigManager.fill.get()) {
            RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 30 / 255F);
            solidBox.setShader(viewMatrix, projMatrix, shader);
        }
        if (!fillOnly) {
            RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F);
            outlinedBox.setShader(viewMatrix, projMatrix, shader);
        }

        stack.pop();
    }

    private boolean playerInsideBoundingBox(OffsetBox nudge) {
        return nudge.getMin().getX() < 0 && nudge.getMax().getX() > 0 &&
                nudge.getMin().getY() < 0 && nudge.getMax().getY() > 0 &&
                nudge.getMin().getZ() < 0 && nudge.getMax().getZ() > 0;
    }

    void renderLine(MatrixStack matrixStack, OffsetPoint startPoint, OffsetPoint endPoint, Color color) {
        if (!RenderCulling.isVisibleCulling(new OffsetBox(startPoint, endPoint).toBox())) return; // TODO better culling
        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.55f);
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION);
        bufferBuilder
                .vertex(matrixStack.peek().getModel(),
                        (float) startPoint.getX() - regionX,
                        (float) startPoint.getY(),
                        (float) startPoint.getZ() - regionZ)
                .next();
        bufferBuilder
                .vertex(matrixStack.peek().getModel(),
                        (float) endPoint.getX() - regionX,
                        (float) endPoint.getY(),
                        (float) endPoint.getZ() - regionZ)
                .next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);

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
        RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.55f);
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP,
                VertexFormats.POSITION);

        Point firstPoint = null;

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            final Point point = center.offset(Math.cos(phi) * radius, dy, Math.sin(phi) * radius);
            if (firstPoint == null) firstPoint = point;
            bufferBuilder.vertex(matrixStack.peek().getModel(),
                    (float) point.getX() - regionX,
                    (float) point.getY(),
                    (float) point.getZ() - regionZ)
                    .next();
        }

        bufferBuilder.vertex(matrixStack.peek().getModel(),
                (float) firstPoint.getX() - regionX,
                (float) firstPoint.getY(),
                (float) firstPoint.getZ() - regionZ)
                .next();

        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        matrixStack.pop();
    }

    private void renderDotSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (!RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius))) return;
        matrixStack.push();
        RenderHelper.applyRegionalRenderOffset(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.55f);
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION);

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            double dy = radius * Math.cos(phi);
            double radiusBySinPhi = radius * Math.sin(phi);
            for (double theta = 0.0D; theta < PI; theta += THETA_SEGMENT) {
                double dx = radiusBySinPhi * Math.cos(theta);
                double dz = radiusBySinPhi * Math.sin(theta);

                final Point point = center.offset(dx, dy, dz);
                bufferBuilder
                        .vertex(matrixStack.peek().getModel(),
                                (float) point.getX() - regionX,
                                (float) point.getY(),
                                (float) point.getZ() - regionZ)
                        .next();
            }
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        matrixStack.pop();
    }
}
