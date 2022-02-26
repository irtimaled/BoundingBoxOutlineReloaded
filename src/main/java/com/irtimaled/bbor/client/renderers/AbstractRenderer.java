package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Camera;
import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
        matrixStack.push();

        renderCuboid0(matrixStack, bb.nudge(), color, fillOnly, fillAlpha, false);

        matrixStack.pop();
    }

    private void renderCuboid0(MatrixStack stack, OffsetBox nudge, Color color, boolean fillOnly, int fillAlpha, boolean mask) {
        if (ConfigManager.fastRender.get() >= 1 && !RenderCulling.isVisibleCulling(nudge.toBox())) return;
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
            RenderBatch.drawSolidBox(stack.peek(), ORIGIN_BOX, color, fillAlpha, mask, minX == maxX, minY == maxY, minZ == maxZ);
        }
        if (!fillOnly) {
            stack.push();
            stack.peek().getPositionMatrix().load(lastStack.getPositionMatrix());
            stack.peek().getNormalMatrix().load(lastStack.getNormalMatrix());
            renderLine(stack, new OffsetPoint(minX, minY, minZ), new OffsetPoint(maxX, minY, minZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, minY, minZ), new OffsetPoint(maxX, minY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, minY, maxZ), new OffsetPoint(minX, minY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(minX, minY, maxZ), new OffsetPoint(minX, minY, minZ), color, true);
            renderLine(stack, new OffsetPoint(minX, minY, minZ), new OffsetPoint(minX, maxY, minZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, minY, minZ), new OffsetPoint(maxX, maxY, minZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, minY, maxZ), new OffsetPoint(maxX, maxY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(minX, minY, maxZ), new OffsetPoint(minX, maxY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(minX, maxY, minZ), new OffsetPoint(maxX, maxY, minZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, maxY, minZ), new OffsetPoint(maxX, maxY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(maxX, maxY, maxZ), new OffsetPoint(minX, maxY, maxZ), color, true);
            renderLine(stack, new OffsetPoint(minX, maxY, maxZ), new OffsetPoint(minX, maxY, minZ), color, true);
            stack.pop();
        }

        stack.pop();
    }

    private boolean playerInsideBoundingBox(OffsetBox nudge) {
        return nudge.getMin().getX() < 0 && nudge.getMax().getX() > 0 &&
                nudge.getMin().getY() < 0 && nudge.getMax().getY() > 0 &&
                nudge.getMin().getZ() < 0 && nudge.getMax().getZ() > 0;
    }


    void renderLine(MatrixStack matrixStack, OffsetPoint startPoint, OffsetPoint endPoint, Color color, boolean cullIfEmpty) {
//        if ((startPoint.getY() == endPoint.getY() && startPoint.getZ() == endPoint.getZ()) ||
//                (startPoint.getX() == endPoint.getX() && startPoint.getZ() == endPoint.getZ()) ||
//                (startPoint.getX() == endPoint.getX() && startPoint.getY() == endPoint.getY())) {
//            renderCuboid0(matrixStack, new OffsetBox(startPoint.offset(-getLineWidth(), -getLineWidth(), -getLineWidth()), endPoint.offset(getLineWidth(), getLineWidth(), getLineWidth())), color, true, 255, true);
//            return;
//        }

        if (cullIfEmpty && startPoint.equals(endPoint)) return;
        if (ConfigManager.fastRender.get() >= 1 && !RenderCulling.isVisibleCulling(new OffsetBox(startPoint, endPoint).toBox())) return; // TODO better culling

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
            renderFilledSphere(matrixStack, center, radius, color);
        }
    }

    private void renderFilledSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (ConfigManager.fastRender.get() >= 1 && !RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius)))
            return;

//        double offset = ((radius - (int) radius) == 0) ? center.getY() - (int) center.getY() : 0;

        final ObjectArrayList<Point> top = new ObjectArrayList<>();
        final ObjectArrayList<Point> bottom = new ObjectArrayList<>();
        final Point topPoint = new Point(center.getX(), center.getY() + radius, center.getZ());
        final Point bottomPoint = new Point(center.getX(), center.getY() - radius, center.getZ());
        for (int i = 0; i < 360; i += 4) {
            top.add(topPoint);
            bottom.add(bottomPoint);
        }

//        double stepModifier = Math.min(32.0 / radius, 3.5D);

//        int dyStep = radius < 64 ? 1 : MathHelper.floor(radius / 32);

        final ObjectArrayList<ObjectArrayList<Point>> points = new ObjectArrayList<>();
//        renderCircle(matrixStack, center, 0.1D, color, offset + radius, top);
        points.add(bottom);
        for (int i = -90; i <= 90; i += 4) {
            double phi = Math.PI / 180 * i;
            double dy = Math.sin(phi) * radius;
            double circleRadius = Math.cos(phi) * radius;
            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
            final ObjectArrayList<Point> pointsCache = new ObjectArrayList<>();
            renderCircle(matrixStack, center, circleRadius, color, dy + 0.001F, pointsCache);
            points.add(pointsCache);
        }
//        for (double dy = offset + radius + 1; dy >= -radius; dy -= dyStep) {
//            double circleRadius = Math.sqrt((radius * radius) - (dy * dy));
//            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
//            final ObjectArrayList<Point> pointsCache = new ObjectArrayList<>();
//            renderCircle(matrixStack, center, circleRadius, color, dy + 0.001F, pointsCache);
//            points.add(pointsCache);
//        }
//        renderCircle(matrixStack, center, 0.1D, color, offset - radius, bottom);
        points.add(top);

        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        final Boolean doFill = ConfigManager.fill.get();

        for (int i = 0; i < points.size() - 1; i++) {
            final ObjectArrayList<Point> pointsCache1 = points.get(i);
            final ObjectArrayList<Point> pointsCache2 = points.get(i + 1);
            assert pointsCache1.size() == pointsCache2.size();
            Point lastPoint1 = null;
            Point lastPoint2 = null;
            for (int j = 0, pointsCacheSize = pointsCache1.size(); j < pointsCacheSize; j++) {
                Point point1 = pointsCache1.get(j);
                Point point2 = pointsCache2.get(j);
                if (ConfigManager.fastRender.get() >= 1 && RenderCulling.isVisibleCulling(new OffsetBox(point1, point2).toBox()))
                    RenderBatch.drawLine(matrixStack.peek(), point1, point2, color, 255);
                if (doFill && lastPoint1 != null) {
                    if (ConfigManager.fastRender.get() >= 1 && RenderCulling.isVisibleCulling(new OffsetBox(lastPoint1, point2).toBox()))
                        RenderBatch.drawFilledFace(matrixStack.peek(), lastPoint1, lastPoint2, point2, point1, color, 127, false);
                }
                lastPoint1 = point1;
                lastPoint2 = point2;
            }
            if (doFill && lastPoint1 != null) {
                if (ConfigManager.fastRender.get() >= 1 && RenderCulling.isVisibleCulling(new OffsetBox(pointsCache1.get(0), lastPoint2).toBox()))
                    RenderBatch.drawFilledFace(matrixStack.peek(), pointsCache1.get(0), pointsCache2.get(0), lastPoint2, lastPoint1, color, 127, false);
            }
        }

        matrixStack.pop();
    }

    private void renderLineSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (ConfigManager.fastRender.get() >= 1 && !RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius)))
            return;

        double offset = ((radius - (int) radius) == 0) ? center.getY() - (int) center.getY() : 0;
        int dyStep = radius < 64 ? 1 : MathHelper.floor(radius / 32);
        final ObjectArrayList<Point> pointsCache = new ObjectArrayList<>();
        for (double dy = offset - radius; dy <= radius + 1; dy += dyStep) {
            double circleRadius = Math.sqrt((radius * radius) - (dy * dy));
            if (circleRadius == 0) circleRadius = Math.sqrt(2) / 2;
            renderCircle(matrixStack, center, circleRadius, color, dy + 0.001F, pointsCache);
            pointsCache.clear();
        }
    }

    private void renderCircle(MatrixStack matrixStack, Point center, double radius, Color color, double dy, ObjectArrayList<Point> cache) {
        matrixStack.push();

        RenderHelper.applyRegionalRenderOffset(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionShader);

        generateCircle(center, radius, dy, cache);
        Point last = null;
        //noinspection RedundantCast
        for (Object _point : (Object[]) cache.elements()) {
            if (_point != null) {
                Point point = (Point) _point;
                if (last != null) {
                    RenderBatch.drawLine(matrixStack.peek(), last, point, color, 255);
                }
                last = point;
            }
        }
        if (last != null) {
            RenderBatch.drawLine(matrixStack.peek(), last, cache.get(0), color, 255);
        }

        matrixStack.pop();
    }

    private void generateCircle(Point center, double radius, double dy, ObjectArrayList<Point> cache) {
        for (int i = 0; i < 360; i += 4) {
            double phi = Math.PI / 180 * i;
            final Point point = center.offset(Math.cos(phi) * radius, dy, Math.sin(phi) * radius);
            cache.add(point);
        }
    }

    private void renderDotSphere(MatrixStack matrixStack, Point center, double radius, Color color) {
        if (ConfigManager.fastRender.get() >= 1 && !RenderCulling.isVisibleCulling(new Box(new BlockPos(center.getX(), center.getY(), center.getZ())).expand(radius)))
            return;
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
