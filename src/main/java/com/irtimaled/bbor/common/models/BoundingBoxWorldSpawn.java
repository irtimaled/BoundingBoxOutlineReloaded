package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;

public class BoundingBoxWorldSpawn extends BoundingBox {
    private BoundingBoxWorldSpawn(BlockPos minBlockPos, BlockPos maxBlockPos, BoundingBoxType type) {
        super(minBlockPos, maxBlockPos, type);
    }

    public static BoundingBoxWorldSpawn from(BlockPos minBlockPos, BlockPos maxBlockPos, BoundingBoxType type) {
        return new BoundingBoxWorldSpawn(minBlockPos, maxBlockPos, type);
    }
}
