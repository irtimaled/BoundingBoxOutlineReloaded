package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.config.ConfigManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractRenderer<T extends AbstractBoundingBox> {
    private static double TAU = 6.283185307179586D;
    private static double PI = TAU / 2D;

    public abstract void render(T boundingBox);

    void renderCuboid(OffsetBox bb, Color color) {
        if (ConfigManager.fill.get()) {
            renderFilledCuboid(bb, color);
        }
        renderUnfilledCuboid(bb, color);
    }

    private void renderFilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_BLEND);
        renderCuboid(bb.nudge(), color, 30);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(-1.f, -1.f);
    }

    void renderUnfilledCuboid(OffsetBox bb, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        renderCuboid(bb.nudge(), color, 255);
    }

    private void renderCuboid(OffsetBox box, Color color, int alpha) {
        OffsetPoint min = box.getMin();
        OffsetPoint max = box.getMax();

        double minX = min.getX();
        double minY = min.getY();
        double minZ = min.getZ();

        double maxX = max.getX();
        double maxY = max.getY();
        double maxZ = max.getZ();

        Renderer renderer = Renderer.startQuads()
                .setColor(color)
                .setAlpha(alpha)
                .addPoint(minX, minY, minZ)
                .addPoint(maxX, minY, minZ)
                .addPoint(maxX, minY, maxZ)
                .addPoint(minX, minY, maxZ);

        if (minY != maxY) {
            renderer.addPoint(minX, maxY, minZ)
                    .addPoint(maxX, maxY, minZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(minX, maxY, maxZ)

                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, minY, maxZ)

                    .addPoint(minX, minY, minZ)
                    .addPoint(minX, maxY, minZ)
                    .addPoint(maxX, maxY, minZ)
                    .addPoint(maxX, minY, minZ)

                    .addPoint(minX, minY, minZ)
                    .addPoint(minX, minY, maxZ)
                    .addPoint(minX, maxY, maxZ)
                    .addPoint(minX, maxY, minZ)

                    .addPoint(maxX, minY, minZ)
                    .addPoint(maxX, minY, maxZ)
                    .addPoint(maxX, maxY, maxZ)
                    .addPoint(maxX, maxY, minZ);
        }
        renderer.render();
    }

    void renderLine(OffsetPoint startPoint, OffsetPoint endPoint, Color color) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Renderer.startLines()
                .setColor(color)
                .addPoint(startPoint)
                .addPoint(endPoint)
                .render();
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
