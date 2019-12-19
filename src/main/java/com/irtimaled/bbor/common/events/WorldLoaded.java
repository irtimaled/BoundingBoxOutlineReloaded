package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.server.v1_16_R3.WorldData;
import net.minecraft.server.v1_16_R3.WorldServer;

public class WorldLoaded {
    private final DimensionId dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(WorldServer world) {
        WorldData info = world.getWorldData();
        this.dimensionId = DimensionId.from(world.getDimensionKey());
        this.seed = world.getSeed();
        this.spawnX = info.a();
        this.spawnZ = info.c();
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
