package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;

public class BoundingBoxStructure extends BoundingBox {
    private BoundingBoxStructure(BlockPos minBlockPos, BlockPos maxBlockPos, BoundingBoxType type) {
        super(minBlockPos, maxBlockPos, type);
    }

    public static BoundingBoxStructure from(MutableBoundingBox bb, BoundingBoxType type) {
        BlockPos minBlockPos = new BlockPos(bb.minX, bb.minY, bb.minZ);
        BlockPos maxBlockPos = new BlockPos(bb.maxX, bb.maxY, bb.maxZ);
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, type);
    }

    public static BoundingBoxStructure from(int[] bb, BoundingBoxType type) {
        BlockPos minBlockPos = new BlockPos(bb[0], bb[1], bb[2]);
        BlockPos maxBlockPos = new BlockPos(bb[3], bb[4], bb[5]);
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, type);
    }

    public static BoundingBoxStructure from(BlockPos minBlockPos, BlockPos maxBlockPos, BoundingBoxType type) {
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, type);
    }
}
