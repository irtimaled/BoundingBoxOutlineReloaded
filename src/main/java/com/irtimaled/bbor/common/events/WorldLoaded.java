package com.irtimaled.bbor.common.events;

import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class WorldLoaded {
    private final int dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(WorldServer world) {
        WorldInfo info = world.getWorldInfo();
        this.dimensionId = world.getDimension().getType().getId();
        this.seed = info.getSeed();
        this.spawnX = info.getSpawnX();
        this.spawnZ = info.getSpawnZ();
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public long getSeed() {
        return seed;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnZ() {
        return spawnZ;
    }
}
