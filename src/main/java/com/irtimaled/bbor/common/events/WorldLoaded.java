package com.irtimaled.bbor.common.events;

import com.irtimaled.bbor.bukkit.NMS.NMSHelper;
import com.irtimaled.bbor.common.models.DimensionId;
import org.jetbrains.annotations.NotNull;


public class WorldLoaded {

    private final DimensionId dimensionId;
    private final long seed;
    private final int spawnX;
    private final int spawnZ;

    public WorldLoaded(@NotNull Object world) {
        Object info = NMSHelper.worldGetWorldData(world);
        this.dimensionId = DimensionId.from(NMSHelper.worldGetResourceKey(world));
        this.seed = NMSHelper.worldGetSeed(world);
        this.spawnX = NMSHelper.worldDataGetSpawnX(info);
        this.spawnZ = NMSHelper.worldDataGetSpawnZ(info);
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
