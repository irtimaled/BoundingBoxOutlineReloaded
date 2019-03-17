package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.world.dimension.DimensionType;

public class MobSpawnerBroken {
    private final DimensionType dimensionType;
    private final Coords pos;

    public MobSpawnerBroken(DimensionType dimensionType, Coords pos) {
        this.dimensionType = dimensionType;
        this.pos = pos;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public Coords getPos() {
        return pos;
    }
}
