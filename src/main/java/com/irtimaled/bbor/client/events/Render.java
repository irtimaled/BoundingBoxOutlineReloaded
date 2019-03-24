package com.irtimaled.bbor.client.events;

import net.minecraft.world.dimension.DimensionType;

public class Render {
    private DimensionType dimensionType;

    public Render(DimensionType dimensionType) {
        this.dimensionType = dimensionType;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }
}
