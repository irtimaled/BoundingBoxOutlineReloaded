package com.irtimaled.bbor.common.events;

import net.minecraft.world.World;

public class WorldLoaded {
    private final World world;

    public WorldLoaded(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
}
