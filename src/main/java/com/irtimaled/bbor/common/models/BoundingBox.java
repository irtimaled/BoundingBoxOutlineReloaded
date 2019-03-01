package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public abstract class BoundingBox {
    private final BlockPos minBlockPos;
    private final BlockPos maxBlockPos;
    private final BoundingBoxType type;

    protected BoundingBox(BlockPos minBlockPos, BlockPos maxBlockPos, BoundingBoxType type) {
        this.minBlockPos = minBlockPos;
        this.maxBlockPos = maxBlockPos;
        this.type = type;
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
        return minBlockPos.equals(other.minBlockPos) && maxBlockPos.equals(other.maxBlockPos);
    }

    @Override
    public String toString() {
        return "(" + minBlockPos.toString() + "; " + maxBlockPos.toString() + ")";
    }

    public AxisAlignedBB toAxisAlignedBB() {
        return toAxisAlignedBB(true);
    }

    public AxisAlignedBB toAxisAlignedBB(boolean extendMaxByOne) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(minBlockPos, maxBlockPos);
        if (extendMaxByOne)
            return axisAlignedBB.expand(1, 1, 1);
        return axisAlignedBB;
    }

    public BlockPos getMinBlockPos() {
        return minBlockPos;
    }

    public BlockPos getMaxBlockPos() {
        return maxBlockPos;
    }

    public Color getColor() {
        return type.getColor();
    }

    public Boolean shouldRender() { return type.shouldRender(); }

    public String getTypeName() {
        return type.getName();
    }
}
