package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.NotNull;


public class WorldLoaded {

    private final DimensionId dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(@NotNull WorldServer world) {
        WorldData info = world.n_();
        this.dimensionId = DimensionId.from(world.aa());
        this.seed = world.D();
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
