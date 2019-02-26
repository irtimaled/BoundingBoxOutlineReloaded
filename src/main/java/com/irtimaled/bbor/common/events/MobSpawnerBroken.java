package com.irtimaled.bbor.common.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class MobSpawnerBroken {
    private final DimensionType dimensionType;
    private final BlockPos pos;

    public MobSpawnerBroken(DimensionType dimensionType, BlockPos pos) {
        this.dimensionType = dimensionType;
        this.pos = pos;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public BlockPos getPos() {
        return pos;
    }
}
