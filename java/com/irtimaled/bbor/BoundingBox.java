package com.irtimaled.bbor;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.awt.*;

public abstract class BoundingBox {
    private final Color color;
    private final BlockPos minBlockPos;
    private final BlockPos maxBlockPos;

    protected BoundingBox(BlockPos minBlockPos, BlockPos maxBlockPos, Color color) {
        this.minBlockPos = minBlockPos;
        this.maxBlockPos = maxBlockPos;
        this.color = color;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + minBlockPos.hashCode();
        result = prime * result + maxBlockPos.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BoundingBox other = (BoundingBox) obj;
        if (!minBlockPos.equals(other.minBlockPos))
            return false;
        if (!maxBlockPos.equals(other.maxBlockPos))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + minBlockPos.toString() + "; " + maxBlockPos.toString() + ")";
    }

    public AxisAlignedBB toAxisAlignedBB() {
        return AxisAlignedBB.fromBounds(minBlockPos.getX(),
                minBlockPos.getY(),
                minBlockPos.getZ(),
                maxBlockPos.getX(),
                maxBlockPos.getY(),
                maxBlockPos.getZ());
    }

    public BlockPos getMinBlockPos() {
        return minBlockPos;
    }

    public BlockPos getMaxBlockPos() {
        return maxBlockPos;
    }

    public Color getColor() {
        return color;
    }
}