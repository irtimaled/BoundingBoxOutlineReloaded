package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.BlockPos;

public class BoundingBoxMobSpawner extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;

    private BoundingBoxMobSpawner(BlockPos center, Integer radius, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, BoundingBoxType.MobSpawner);
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
        return new BoundingBoxMobSpawner(center, 16, minBlockPos, maxBlockPos);
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
