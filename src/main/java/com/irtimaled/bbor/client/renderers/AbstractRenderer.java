package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
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

    void renderCuboid(MatrixStack matrixStack, OffsetBox bb, Color color) {
        OffsetBox nudge = bb;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        matrixStack.push();

        renderCuboidSolid(matrixStack, nudge, color);

        matrixStack.pop();

        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    protected void renderCuboidSolid(MatrixStack stack, OffsetBox nudge, Color color) {
        if (ConfigManager.invertBoxColorPlayerInside.get() &&
                playerInsideBoundingBox(nudge)) {
            color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }
        stack.push();
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;
        RenderHelper.applyRegionalRenderOffset(stack);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        stack.translate(nudge.getMin().getX() - regionX, nudge.getMin().getY(), nudge.getMin().getZ() - regionZ);
        stack.scale((float) (nudge.getMax().getX() - nudge.getMin().getX()),
                (float) (nudge.getMax().getY() - nudge.getMin().getY()),
                (float) (nudge.getMax().getZ() - nudge.getMin().getZ()));

        Matrix4f viewMatrix = stack.peek().getModel();
        Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
        Shader shader = RenderSystem.getShader();
        RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getRed() / 255F, 0.25F);
        solidBox.setShader(viewMatrix, projMatrix, shader);
        RenderSystem.setShaderColor(color.getRed() / 255F, color.getGreen() / 255F, color.getRed() / 255F, 0.5F);
        outlinedBox.setShader(viewMatrix, projMatrix, shader);

        stack.pop();
    }

    private boolean playerInsideBoundingBox(OffsetBox nudge) {
        return nudge.getMin().getX() < 0 && nudge.getMax().getX() > 0 &&
                nudge.getMin().getY() < 0 && nudge.getMax().getY() > 0 &&
                nudge.getMin().getZ() < 0 && nudge.getMax().getZ() > 0;
    }

    void renderLine(MatrixStack matrixStack, OffsetPoint startPoint, OffsetPoint endPoint, Color color) {
        RenderHelper.polygonModeLine();
        Renderer.startLines()
                .setMatrixStack(matrixStack)
                .setColor(color)
                .addPoint(startPoint)
                .addPoint(endPoint)
                .render();
    }

    void renderFilledFaces(MatrixStack matrixStack, OffsetPoint min, OffsetPoint max, Color color) {
        renderFilledFaces(matrixStack, min, max, color, 30);
    }

    void renderFilledFaces(MatrixStack matrixStack, OffsetPoint min, OffsetPoint max, Color color, int alpha) {
        if (!ConfigManager.fill.get()) return;
        // RenderQueue.deferRendering(() -> renderFaces(matrixStack, min, max, color, alpha, Renderer::startQuads));
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
        Renderer renderer = Renderer.startLineLoop()
                .setMatrixStack(matrixStack)
                .setColor(color);

        for (double phi = 0.0D; phi < TAU; phi += PHI_SEGMENT) {
            renderer.addPoint(new OffsetPoint(center.offset(Math.cos(phi) * radius, dy, Math.sin(phi) * radius)));
        }

        renderer.render();
    }

    private void renderDotSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        Renderer renderer = Renderer.startQuads()
                .setMatrixStack(matrixStack)
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
