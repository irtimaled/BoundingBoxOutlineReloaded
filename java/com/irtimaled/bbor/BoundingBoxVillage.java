package com.irtimaled.bbor;

import net.minecraft.util.BlockPos;

import java.awt.*;

public class BoundingBoxVillage extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;
    private final boolean spawnsIronGolems;

    protected BoundingBoxVillage(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
        this.spawnsIronGolems = spawnsIronGolems;
    }


    public static BoundingBox from(BlockPos center, Integer radius, boolean spawnsIronGolems, Color color) {
        BlockPos minBlockPos = new BlockPos(center.getX() - radius,
                center.getY() - 4,
                center.getZ() - radius);
        BlockPos maxBlockPos = new BlockPos(center.getX() + radius,
                center.getY() + 4,
                center.getZ() + radius);
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, minBlockPos, maxBlockPos);
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

    public boolean getSpawnsIronGolems() {
        return spawnsIronGolems;
    }
}