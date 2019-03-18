package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.Coords;

public class MobSpawnerBroken {
    private final int dimensionId;
    private final Coords pos;

    public MobSpawnerBroken(int dimensionId, Coords pos) {
        this.dimensionId = dimensionId;
        this.pos = pos;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public Coords getPos() {
        return pos;
    }
}
