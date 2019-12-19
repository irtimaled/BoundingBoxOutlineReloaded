package com.irtimaled.bbor.common.events;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.LevelProperties;

public class WorldLoaded {
    private final int dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(ServerWorld world) {
        LevelProperties info = world.getLevelProperties();
        this.dimensionId = world.getDimension().getType().getRawId();
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
