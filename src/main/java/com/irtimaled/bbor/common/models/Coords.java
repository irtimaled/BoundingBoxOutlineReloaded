package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.MathHelper;
import net.minecraft.util.math.BlockPos;

public class Coords {
    private final int x;
    private final int y;
    private final int z;

    public Coords(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coords(double x, double y, double z) {
        this.x = MathHelper.floor(x);
        this.y = MathHelper.floor(y);
        this.z = MathHelper.floor(z);
    }

    public Coords(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Coords)) {
            return false;
        }

        Coords coords = (Coords)other;
        return this.getX() == coords.getX() &&
                this.getY() == coords.getY() &&
                this.getZ() == coords.getZ();
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }
}

