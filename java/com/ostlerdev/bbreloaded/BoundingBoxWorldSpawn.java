package com.ostlerdev.bbreloaded;

import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class BoundingBoxWorldSpawn extends BoundingBox {

    protected BoundingBoxWorldSpawn(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        super(minBlockPos, maxBlockPos, color);
    }

    public static BoundingBoxWorldSpawn from(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        return new BoundingBoxWorldSpawn(minBlockPos, maxBlockPos, color);
    }
}
