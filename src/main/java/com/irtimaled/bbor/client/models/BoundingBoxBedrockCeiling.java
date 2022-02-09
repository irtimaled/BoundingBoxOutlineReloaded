package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.client.renderers.BedrockCeilingRenderer;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxBedrockCeiling extends BoundingBoxCuboid {
    private static final AbstractRenderer<BoundingBoxBedrockCeiling> RENDERER = CommonInterop.registerRenderer(BoundingBoxBedrockCeiling.class, () -> new BedrockCeilingRenderer());

    public BoundingBoxBedrockCeiling(Coords coords) {
        super(coords, coords, BoundingBoxType.BedrockCeiling);
    }

    @Override
    public AbstractRenderer<?> getRenderer() {
        return RENDERER;
    }
}
