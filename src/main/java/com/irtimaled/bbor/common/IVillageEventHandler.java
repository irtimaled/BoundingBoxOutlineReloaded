package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.BoundingBox;
import net.minecraft.world.dimension.DimensionType;

public interface IVillageEventHandler {
    void villageRemoved(DimensionType dimensionType, BoundingBox bb);
}
