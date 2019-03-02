package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.WorldData;
import net.minecraft.world.dimension.DimensionType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DimensionCache {
    private final Map<DimensionType, BoundingBoxCache> map = new ConcurrentHashMap<>();
    private WorldData worldData;

    public BoundingBoxCache getCache(DimensionType dimensionType) {
        return map.get(dimensionType);
    }

    public void delegate(DimensionType dimensionType, Consumer<BoundingBoxCache> action) {
        action.accept(getOrCreateCache(dimensionType));
    }

    public BoundingBoxCache getOrCreateCache(DimensionType dimensionType) {
        return map.computeIfAbsent(dimensionType, dt -> new BoundingBoxCache());
    }

    public void clear() {
        worldData = null;
        for (BoundingBoxCache cache : map.values()) {
            cache.close();
        }
        map.clear();
    }

    void setWorldData(long seed, int spawnX, int spawnZ) {
        this.worldData = new WorldData(seed, spawnX, spawnZ);
    }

    public WorldData getWorldData() {
        return worldData;
    }
}
