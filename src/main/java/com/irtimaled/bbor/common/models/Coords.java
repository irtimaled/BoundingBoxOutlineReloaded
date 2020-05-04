package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.TypeHelper;
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
    public int hashCode() {
        return TypeHelper.combineHashCodes(z, y, x);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coords other = (Coords) obj;
        return getX() == other.getX() &&
                getY() == other.getY() &&
                getZ() == other.getZ();

    }
}

