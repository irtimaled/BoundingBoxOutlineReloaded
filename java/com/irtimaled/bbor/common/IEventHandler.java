package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.DimensionType;

public interface IEventHandler {
    void boundingBoxRemoved(DimensionType dimensionType, BoundingBox bb);
}
