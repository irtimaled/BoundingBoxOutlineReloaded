package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class VillageRenderer extends Renderer<BoundingBoxVillage> {
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
        BlockPos center = boundingBox.getCenter();
        AxisAlignedBB abb = new AxisAlignedBB(new BlockPos(center.getX() - 8,
                center.getY() - 3,
                center.getZ() - 8),
                new BlockPos(center.getX() + 8,
                        center.getY() + 3,
                        center.getZ() + 8))
                .offset(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());

        renderCuboid(abb, boundingBox.getColor(), false);
    }

    private void renderVillageDoors(BoundingBoxVillage boundingBox) {
        OffsetPoint center = new OffsetPoint(boundingBox.getCenter())
                .add(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());
        Color color = boundingBox.getColor();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        int colorR = color.getRed();
        int colorG = color.getGreen();
        int colorB = color.getBlue();

        worldRenderer.begin(GL11.GL_LINES, worldRenderer.getVertexFormat());
        for (BlockPos door : boundingBox.getDoors()) {
            OffsetPoint point = new OffsetPoint(door).add(0.5, 0, 0.5);

            worldRenderer.pos(point.getX(), point.getY(), point.getZ()).color(colorR, colorG, colorB, 255).endVertex();
            worldRenderer.pos(center.getX(), center.getY(), center.getZ()).color(colorR, colorG, colorB, 255).endVertex();
        }
        tessellator.draw();
    }

    private void renderBoundingBoxVillageAsSphere(BoundingBoxVillage boundingBox) {
        OffsetPoint center = new OffsetPoint(boundingBox.getCenter())
                .add(boundingBox.getCenterOffsetX(), 0.0, boundingBox.getCenterOffsetZ());
        int radius = boundingBox.getRadius();
        Color color = boundingBox.getColor();
        renderSphere(center, radius, color);
    }

    private void renderSphere(OffsetPoint center, double radius, Color color) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(ConfigManager.villageSphereDotSize.get());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (OffsetPoint point : buildPoints(center, radius)) {
            worldRenderer.pos(point.getX(), point.getY(), point.getZ())
                    .color(color.getRed(), color.getGreen(), color.getBlue(), 255)
                    .endVertex();
        }
        tessellator.draw();
    }

    private Set<OffsetPoint> buildPoints(OffsetPoint center, double radius) {
        int density = ConfigManager.villageSphereDensity.get();
        int segments = 24 + (density*8);

        Set<OffsetPoint> points = new HashSet<>(segments*segments);
        double tau = 6.283185307179586D;
        double pi = tau / 2D;

        double thetaSegment = pi / (double)segments;
        double phiSegment = tau / (double)segments;

        for (double phi = 0.0D; phi < tau; phi += phiSegment) {
            for (double theta = 0.0D; theta < pi; theta += thetaSegment) {
                double dx = radius * Math.sin(phi) * Math.cos(theta);
                double dz = radius * Math.sin(phi) * Math.sin(theta);
                double dy = radius * Math.cos(phi);

                points.add(center.add(dx, dy, dz));
            }
        }
        return points;
    }
}
