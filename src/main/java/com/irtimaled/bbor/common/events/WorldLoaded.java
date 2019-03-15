package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.server.v1_14_R1.WorldData;
import net.minecraft.server.v1_14_R1.WorldServer;

public class WorldLoaded {
    private final DimensionId dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(WorldServer world) {
        WorldData info = world.getWorldData();
        this.dimensionId = DimensionId.from(world.worldProvider.getDimensionManager());
        this.seed = info.getSeed();
        this.spawnX = info.b();
        this.spawnZ = info.d();
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
