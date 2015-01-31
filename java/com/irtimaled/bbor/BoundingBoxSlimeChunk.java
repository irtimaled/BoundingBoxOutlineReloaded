package com.irtimaled.bbor;

import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

import java.awt.*;

public class BoundingBoxSlimeChunk extends BoundingBox {
    private BoundingBoxSlimeChunk(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        super(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxSlimeChunk from(ChunkCoordIntPair chunkCoordIntPair, Color color) {
        BlockPos minBlockPos = new BlockPos(chunkCoordIntPair.getXStart(), 1, chunkCoordIntPair.getZStart());
        BlockPos maxBlockPos = new BlockPos(chunkCoordIntPair.getXEnd(), 38, chunkCoordIntPair.getZEnd());
        return new BoundingBoxSlimeChunk(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxSlimeChunk from(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        return new BoundingBoxSlimeChunk(minBlockPos, maxBlockPos, color);
    }
}