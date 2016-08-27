package com.irtimaled.bbor;

import net.minecraft.world.DimensionType;

public interface IEventHandler {
    void boundingBoxRemoved(DimensionType dimensionType, BoundingBox bb);
}
