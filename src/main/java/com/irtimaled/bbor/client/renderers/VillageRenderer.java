package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class VillageRenderer extends AbstractRenderer<BoundingBoxVillage> {
    @Override
    public void render(BoundingBoxVillage boundingBox) {
        if (ConfigManager.renderVillageAsSphere.get()) {
            renderBoundingBoxVillageAsSphere(boundingBox);
        } else {
            renderBoundingBox(boundingBox);
        }
        if (ConfigManager.drawIronGolemSpawnArea.get() &&
                boundingBox.getSpawnsIronGolems()) {
            renderIronGolemSpawnArea(boundingBox);
        }
        if (ConfigManager.drawVillageDoors.get()) {
            renderVillageDoors(boundingBox);
        }
    }

    private void renderIronGolemSpawnArea(BoundingBoxVillage boundingBox) {
        OffsetPoint offsetCenter = new OffsetPoint(boundingBox.getCenter())
                .offset(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());
        OffsetBox bb = new OffsetBox(offsetCenter, offsetCenter)
                .grow(8, 3, 8);

        renderUnfilledCuboid(bb, boundingBox.getColor());
    }

    private void renderVillageDoors(BoundingBoxVillage boundingBox) {
        OffsetPoint center = new OffsetPoint(boundingBox.getCenter())
                .offset(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());
        Color color = boundingBox.getColor();

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Renderer renderer = Renderer.startLines()
                .setColor(color);
        for (Coords door : boundingBox.getDoors()) {
            OffsetPoint point = new OffsetPoint(door).offset(0.5, 0, 0.5);

            renderer.addPoint(point)
                    .addPoint(center);
        }
        renderer.render();
    }

    private void renderBoundingBoxVillageAsSphere(BoundingBoxVillage boundingBox) {
        OffsetPoint center = new OffsetPoint(boundingBox.getCenter())
                .offset(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());
        int radius = boundingBox.getRadius();
        Color color = boundingBox.getColor();
        int density = ConfigManager.villageSphereDensity.get();
        int dotSize = ConfigManager.villageSphereDotSize.get();

        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(dotSize);
        Renderer renderer = Renderer.startPoints()
                .setColor(color);
        buildPoints(center, (double) radius, density)
                .forEach(renderer::addPoint);
        renderer.render();
    }

    private Set<OffsetPoint> buildPoints(OffsetPoint center, double radius, int density) {
        int segments = 24 + (density * 8);

        Set<OffsetPoint> points = new HashSet<>(segments * segments);
        double tau = 6.283185307179586D;
        double pi = tau / 2D;

        double thetaSegment = pi / (double) segments;
        double phiSegment = tau / (double) segments;

        for (double phi = 0.0D; phi < tau; phi += phiSegment) {
            for (double theta = 0.0D; theta < pi; theta += thetaSegment) {
                double dx = radius * Math.sin(phi) * Math.cos(theta);
                double dz = radius * Math.sin(phi) * Math.sin(theta);
                double dy = radius * Math.cos(phi);

                points.add(center.offset(dx, dy, dz));
            }
        }
        return points;
    }
}
