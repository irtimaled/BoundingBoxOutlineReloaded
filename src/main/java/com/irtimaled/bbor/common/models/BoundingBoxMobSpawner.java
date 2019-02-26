package com.irtimaled.bbor.common.models;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class BoundingBoxMobSpawner extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;

    private BoundingBoxMobSpawner(BlockPos center, Integer radius, Color color, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
    }

    public static BoundingBoxMobSpawner from(BlockPos center) {
        BlockPos minBlockPos = new BlockPos(center.getX() - 5,
                center.getY() - 1,
                center.getZ() - 5);
        BlockPos maxBlockPos = new BlockPos(center.getX() + 5,
                center.getY() + 2,
                center.getZ() + 5);
        return new BoundingBoxMobSpawner(center, 16, Color.GREEN, minBlockPos, maxBlockPos);
    }

    @Override
    public String toString() {
        return "(" + center.toString() + "; " + radius.toString() + ")";
    }

    public Integer getRadius() {
        return radius;
    }

    public BlockPos getCenter() {
        return center;
    }
}
