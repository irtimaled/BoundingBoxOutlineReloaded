package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.common.models.BoundingBoxVillage;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.config.ConfigManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class VillageRenderer extends AbstractRenderer<BoundingBoxVillage> {
    @Override
    public void render(BoundingBoxVillage boundingBox) {
        renderBoundingBoxVillageAsSphere(boundingBox);
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

        renderSphere(center, radius, color, density, dotSize);
    }
}
