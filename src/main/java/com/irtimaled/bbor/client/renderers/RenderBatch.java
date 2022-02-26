package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import com.irtimaled.bbor.client.models.Point;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;

public class RenderBatch {

    private static final BufferBuilder quadBufferBuilderNonMasked = new BufferBuilder(2097152);
    private static final BufferBuilder quadBufferBuilderMasked = new BufferBuilder(2097152);
    private static final BufferBuilder lineBufferBuilder = new BufferBuilder(2097152);

    private static final Object mutex = new Object();
    private static final AtomicLong quadNonMaskedCount = new AtomicLong(0L);
    private static final AtomicLong quadMaskedCount = new AtomicLong(0L);
    private static final AtomicLong lineCount = new AtomicLong(0L);
    private static final AtomicLong quadNonMaskedCountLast = new AtomicLong(0L);
    private static final AtomicLong quadMaskedCountLast = new AtomicLong(0L);
    private static final AtomicLong lineCountLast = new AtomicLong(0L);

    private static final AtomicLong lastDurationNanos = new AtomicLong(0L);

    static void beginBatch() {
        quadBufferBuilderMasked.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        quadBufferBuilderNonMasked.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        lineBufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
    }

    public static void drawSolidBox(MatrixStack.Entry matrixEntry, Box box, Color color, int alpha, boolean mask, boolean sameX, boolean sameY, boolean sameZ) {
        final float minX = (float) box.minX;
        final float minY = (float) box.minY;
        final float minZ = (float) box.minZ;
        final float maxX = (float) box.maxX;
        final float maxY = (float) box.maxY;
        final float maxZ = (float) box.maxZ;
        final int red = color.getRed();
        final int green = color.getGreen();
        final int blue = color.getBlue();

        final BufferBuilder bufferBuilder = mask ? RenderBatch.quadBufferBuilderMasked : RenderBatch.quadBufferBuilderNonMasked;

        if (!sameX && !sameZ) {
            if (mask) quadMaskedCount.getAndIncrement();
            else quadNonMaskedCount.getAndIncrement();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();
            if (!sameY) {
                if (mask) quadMaskedCount.getAndIncrement();
                else quadNonMaskedCount.getAndIncrement();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();
            }
        }

        if (!sameX && !sameY) {
            if (mask) quadMaskedCount.getAndIncrement();
            else quadNonMaskedCount.getAndIncrement();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
            if (!sameZ) {
                if (mask) quadMaskedCount.getAndIncrement();
                else quadNonMaskedCount.getAndIncrement();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();
            }
        }

        if (!sameY && !sameZ) {
            if (mask) quadMaskedCount.getAndIncrement();
            else quadNonMaskedCount.getAndIncrement();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(matrixEntry.getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
            if (!sameX) {
                if (mask) quadMaskedCount.getAndIncrement();
                else quadNonMaskedCount.getAndIncrement();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
                bufferBuilder.vertex(matrixEntry.getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();
            }
        }
    }

    public static void drawFilledFace(MatrixStack.Entry matrixEntry, Point point1, Point point2, Point point3, Point point4, Color color, int alpha, boolean mask) {
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;

        if (mask) quadMaskedCount.getAndIncrement();
        else quadNonMaskedCount.getAndIncrement();

        final BufferBuilder bufferBuilder = mask ? RenderBatch.quadBufferBuilderMasked : RenderBatch.quadBufferBuilderNonMasked;

        final float x1 = (float) point1.getX() - regionX;
        final float y1 = (float) point1.getY();
        final float z1 = (float) point1.getZ() - regionZ;
        bufferBuilder.vertex(matrixEntry.getPositionMatrix(), x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).next();

        final float x2 = (float) point2.getX() - regionX;
        final float y2 = (float) point2.getY();
        final float z2 = (float) point2.getZ() - regionZ;
        bufferBuilder.vertex(matrixEntry.getPositionMatrix(), x2, y2, z2).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).next();

        final float x3 = (float) point3.getX() - regionX;
        final float y3 = (float) point3.getY();
        final float z3 = (float) point3.getZ() - regionZ;
        bufferBuilder.vertex(matrixEntry.getPositionMatrix(), x3, y3, z3).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).next();

        final float x4 = (float) point4.getX() - regionX;
        final float y4 = (float) point4.getY();
        final float z4 = (float) point4.getZ() - regionZ;
        bufferBuilder.vertex(matrixEntry.getPositionMatrix(), x4, y4, z4).color(color.getRed(), color.getGreen(), color.getBlue(), alpha).next();
    }

    public static void drawLine(MatrixStack.Entry matrixEntry, Point startPoint, Point endPoint, Color color, int alpha) {
        int regionX = (((int) Camera.getX()) >> 9) * 512;
        int regionZ = (((int) Camera.getZ()) >> 9) * 512;

        lineCount.getAndIncrement();

        lineBufferBuilder
                .vertex(matrixEntry.getPositionMatrix(),
                        (float) startPoint.getX() - regionX,
                        (float) startPoint.getY(),
                        (float) startPoint.getZ() - regionZ)
                .color(color.getRed(), color.getGreen(), color.getBlue(), alpha)
                .next();
        lineBufferBuilder
                .vertex(matrixEntry.getPositionMatrix(),
                        (float) endPoint.getX() - regionX,
                        (float) endPoint.getY(),
                        (float) endPoint.getZ() - regionZ)
                .color(color.getRed(), color.getGreen(), color.getBlue(), alpha)
                .next();
    }

    static void endBatch() {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        long startTime = System.nanoTime();
        quadBufferBuilderMasked.end();
        quadBufferBuilderNonMasked.end();
        lineBufferBuilder.end();

        synchronized (mutex) {
            quadMaskedCountLast.set(quadMaskedCount.get());
            quadNonMaskedCountLast.set(quadNonMaskedCount.get());
            lineCountLast.set(lineCount.get());
            quadMaskedCount.set(0);
            quadNonMaskedCount.set(0);
            lineCount.set(0);
        }

        RenderSystem.depthMask(true);
        BufferRenderer.draw(quadBufferBuilderMasked);
        BufferRenderer.draw(lineBufferBuilder);

        RenderSystem.depthMask(false);
        BufferRenderer.draw(quadBufferBuilderNonMasked);
        lastDurationNanos.set(System.nanoTime() - startTime);

        RenderSystem.depthMask(true);
    }

    public static String debugString() {
        return String.format("[BBOR] Statistics: Filled faces: %d+%d Lines: %d @ %.2fms", quadMaskedCountLast.get(), quadNonMaskedCountLast.get(), lineCountLast.get(), lastDurationNanos.get() / 1_000_000.0);
    }

}
