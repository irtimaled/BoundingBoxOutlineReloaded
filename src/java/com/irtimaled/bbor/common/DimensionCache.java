package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.WorldData;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DimensionCache {
    private final Map<DimensionType, BoundingBoxCache> map = new ConcurrentHashMap<>();
    private WorldData worldData;

    void refresh(DimensionType dimensionType) {
        if (map.containsKey(dimensionType)) {
            map.get(dimensionType).refresh();
        }
    }

    public void put(DimensionType dimensionType, BoundingBoxCache boundingBoxCache) {
        map.put(dimensionType, boundingBoxCache);
    }

    public BoundingBoxCache getBoundingBoxes(DimensionType dimensionType) {
        return map.get(dimensionType);
    }

    public void clear() {
        worldData = null;
        for (BoundingBoxCache cache : map.values()) {
            cache.close();
        }
        map.clear();
    }

    public void setWorldData(long seed, int spawnX, int spawnZ) {
        this.worldData = new WorldData(seed, spawnX, spawnZ);
    }

    public WorldData getWorldData() {
        return worldData;
    }
}
