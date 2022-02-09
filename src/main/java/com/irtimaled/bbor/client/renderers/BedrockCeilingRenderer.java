package com.irtimaled.bbor.client.renderers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxBedrockCeiling;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.util.math.MatrixStack;

public class BedrockCeilingRenderer extends AbstractRenderer<BoundingBoxBedrockCeiling> {
    @Override
    public void render(MatrixStack matrixStack, BoundingBoxBedrockCeiling boundingBox) {
        Coords coords = boundingBox.getMinCoords();
        int x = coords.getX();
        int y = coords.getY();
        int z = coords.getZ();
        renderCuboid(matrixStack, new OffsetBox(new OffsetPoint(x, y + 1.01d, z),
                        new OffsetPoint(x + 1, y + 1.01d, z + 1)),
                BoundingBoxTypeHelper.getColor(boundingBox.getType()), true, 127);
    }
}
