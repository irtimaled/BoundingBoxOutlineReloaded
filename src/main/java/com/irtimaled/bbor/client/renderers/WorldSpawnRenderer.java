package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.util.math.MatrixStack;

public class WorldSpawnRenderer extends AbstractRenderer<BoundingBoxWorldSpawn> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxWorldSpawn boundingBox) {
        Coords minCoords = boundingBox.getMinCoords();
        Coords maxCoords = boundingBox.getMaxCoords();

        double y = Player.getMaxY(ConfigManager.worldSpawnMaxY.get());

        OffsetBox offsetBox = new OffsetBox(minCoords.getX(), y, minCoords.getZ(), maxCoords.getX(), y, maxCoords.getZ());
        renderOutlinedCuboid(matrixStack, offsetBox.nudge(), BoundingBoxTypeHelper.getColor(boundingBox.getType()));
    }
}
