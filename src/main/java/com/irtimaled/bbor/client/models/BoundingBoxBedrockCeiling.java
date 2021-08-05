package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.CuboidRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxBedrockCeiling extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxCuboid> RENDERER = CommonInterop.registerRenderer(BoundingBoxBedrockCeiling.class, () -> new CuboidRenderer());

    public BoundingBoxBedrockCeiling(Coords coords) {
        super(coords, coords, BoundingBoxType.BedrockCeiling);
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
