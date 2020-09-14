package com.irtimaled.bbor.client.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

public class BoundingBoxBedrockCeiling extends BoundingBoxCuboid {
    public BoundingBoxBedrockCeiling(Coords coords) {
        super(coords, coords, BoundingBoxType.BedrockCeiling);
    }
}
