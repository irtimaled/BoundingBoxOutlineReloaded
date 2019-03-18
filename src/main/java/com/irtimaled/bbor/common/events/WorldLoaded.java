package com.irtimaled.bbor.common.events;

import net.minecraft.world.WorldServer;

public class WorldLoaded {
    private final WorldServer world;

    public WorldLoaded(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }
}
