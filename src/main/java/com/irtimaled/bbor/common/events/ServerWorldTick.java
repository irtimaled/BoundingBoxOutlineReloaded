package com.irtimaled.bbor.common.events;

import net.minecraft.world.WorldServer;

public class ServerWorldTick {
    private WorldServer world;

    public ServerWorldTick(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }
}
