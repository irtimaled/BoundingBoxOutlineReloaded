package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;

public class BoundingBoxSlimeChunk extends BoundingBox {
    private BoundingBoxSlimeChunk(BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, BoundingBoxType.SlimeChunks);
    }

    public static BoundingBoxSlimeChunk from(BlockPos minBlockPos, BlockPos maxBlockPos) {
        return new BoundingBoxSlimeChunk(minBlockPos, maxBlockPos);
    }
}
