package com.irtimaled.bbor;

import net.minecraft.util.BlockPos;
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

    public static BoundingBoxStructure from(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        return new BoundingBoxStructure(minBlockPos, maxBlockPos, color);
    }
}