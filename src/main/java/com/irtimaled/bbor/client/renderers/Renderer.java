package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

@Deprecated
public class Renderer {
    private final VertexFormat.DrawMode glMode;

    static Renderer startLines() {
        return new Renderer(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
    }

    static Renderer startLineLoop() {
        return new Renderer(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
    }

    public static Renderer startQuads() {
        return new Renderer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    }

    public static Renderer startTextured() {
        return new Renderer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    }

    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final BufferBuilder bufferBuilder = tessellator.getBuffer();

    private int red;
    private int green;
    private int blue;
    private int alpha;
    private MatrixStack matrixStack;

    private Renderer(VertexFormat.DrawMode glMode, VertexFormat vertexFormat) {
        bufferBuilder.begin(glMode, vertexFormat);
        this.glMode = glMode;
    }

    public Renderer setMatrixStack(MatrixStack stack) {
        this.matrixStack = stack;
        matrixStack.push();
        return this;
    }

    public Renderer setColor(Color color) {
        return setColor(color.getRed(), color.getGreen(), color.getBlue())
                .setAlpha(color.getAlpha());
    }

    public Renderer setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    public Renderer setAlpha(int alpha) {
        this.alpha = alpha;
        return this;
    }

    Renderer addPoint(OffsetPoint point) {
        return addPoint(point.getX(), point.getY(), point.getZ());
    }

    public Renderer addPoints(OffsetPoint[] points) {
        Renderer renderer = this;
        for (OffsetPoint point : points) {
            renderer = renderer.addPoint(point);
        }
        return renderer;
    }

    public Renderer addPoint(double x, double y, double z) {
        matrixStack.push();
        pos(x, y, z);
        color();
        end();
        matrixStack.pop();
        return this;
    }

    public Renderer addPoint(double x, double y, double z, double u, double v) {
        pos(x, y, z);
        tex(u, v);
        color();
        end();
        return this;
    }

    public void render() {
        if (glMode == VertexFormat.DrawMode.QUADS) {
            bufferBuilder.sortFrom((float) Camera.getX(), (float) Camera.getY(), (float) Camera.getZ());
        }
        tessellator.draw();
        matrixStack.pop();
    }

    private void pos(double x, double y, double z) {
        bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), (float) x, (float) y, (float) z);
    }

    private void tex(double u, double v) {
        bufferBuilder.texture((float) u, (float) v);
    }

    private void color() {
        bufferBuilder.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }

    private void end() {
        bufferBuilder.next();
    }
}
