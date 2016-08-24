package com.irtimaled.bbor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.awt.*;

public class BoundingBoxStructure extends BoundingBox {
    private BoundingBoxStructure(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        super(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxStructure from(StructureBoundingBox bb, Color color) {
        BlockPos minBlockPos = new BlockPos(bb.minX, bb.minY, bb.minZ);
        BlockPos maxBlockPos = new BlockPos(bb.maxX, bb.maxY, bb.maxZ);
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxStructure from(int[] bb, Color color) {
        BlockPos minBlockPos = new BlockPos(bb[0], bb[1], bb[2]);
        BlockPos maxBlockPos = new BlockPos(bb[3], bb[4], bb[5]);
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxStructure from(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, color);
    }
}