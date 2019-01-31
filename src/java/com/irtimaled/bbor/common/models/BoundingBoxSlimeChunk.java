package com.irtimaled.bbor.common.models;

import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class BoundingBoxSlimeChunk extends BoundingBox {
    private BoundingBoxSlimeChunk(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        super(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxSlimeChunk from(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        return new BoundingBoxSlimeChunk(minBlockPos, maxBlockPos, color);
    }
}
