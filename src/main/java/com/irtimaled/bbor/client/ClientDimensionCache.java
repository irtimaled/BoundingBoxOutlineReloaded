package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.DimensionCache;
import com.irtimaled.bbor.common.models.BoundingBox;

import java.util.function.Supplier;

public class ClientDimensionCache extends DimensionCache {
    private BoundingBox worldSpawnBoundingBox;
    private BoundingBox spawnChunksBoundingBox;
    private BoundingBox lazySpawnChunksBoundingBox;

    @Override
    public void setWorldData(long seed, int spawnX, int spawnZ) {
        clearClientCache();
        super.setWorldData(seed, spawnX, spawnZ);
    }

    @Override
    public void clear() {
        clearClientCache();
        super.clear();
    }

    private void clearClientCache() {
        worldSpawnBoundingBox = null;
        spawnChunksBoundingBox = null;
        lazySpawnChunksBoundingBox = null;
    }

    BoundingBox getOrSetSpawnChunks(Supplier<BoundingBox> defaultSupplier) {
        if (spawnChunksBoundingBox == null) {
            spawnChunksBoundingBox = defaultSupplier.get();
        }
        return spawnChunksBoundingBox;
    }

    BoundingBox getOrSetLazySpawnChunks(Supplier<BoundingBox> defaultSupplier) {
        if (lazySpawnChunksBoundingBox == null) {
            lazySpawnChunksBoundingBox = defaultSupplier.get();
        }
        return lazySpawnChunksBoundingBox;
    }

    BoundingBox getOrSetWorldSpawn(Supplier<BoundingBox> defaultSupplier) {
        if (worldSpawnBoundingBox == null) {
            worldSpawnBoundingBox = defaultSupplier.get();
        }
        return worldSpawnBoundingBox;
    }
}
