package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldProperties;

public class WorldLoaded {
    private final DimensionId dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(ServerWorld world) {
        WorldProperties info = world.getLevelProperties();
        this.dimensionId = DimensionId.from(world.getRegistryKey());
        this.seed = world.getSeed();
        this.spawnX = info.getSpawnX();
        this.spawnZ = info.getSpawnZ();
    }

    public DimensionId getDimensionId() {
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
