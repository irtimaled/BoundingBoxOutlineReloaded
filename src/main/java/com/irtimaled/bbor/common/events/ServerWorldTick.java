package com.irtimaled.bbor.common.events;

import net.minecraft.world.WorldServer;

public class ServerWorldTick {
    private final int dimensionId;
    private final WorldServer world;

    public ServerWorldTick(WorldServer world) {
        this.world = world;
        this.dimensionId = world.getDimension().getType().getId();
    }

    public WorldServer getWorld() {
        return world;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
