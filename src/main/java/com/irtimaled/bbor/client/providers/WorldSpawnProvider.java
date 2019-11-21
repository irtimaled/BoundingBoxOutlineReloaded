package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.HashSet;
import java.util.Set;

public class WorldSpawnProvider implements IBoundingBoxProvider<BoundingBoxWorldSpawn>, ICachingProvider {
    private static final double CHUNK_SIZE = 16d;
    private static BoundingBoxWorldSpawn spawnChunks;
    private static BoundingBoxWorldSpawn lazyChunks;
    private static BoundingBoxWorldSpawn worldSpawn;

    public static void setWorldSpawn(int spawnX, int spawnZ) {
        worldSpawn = getWorldSpawnBoundingBox(spawnX, spawnZ);
        spawnChunks = buildSpawnChunksBoundingBox(spawnX, spawnZ, 19, BoundingBoxType.SpawnChunks);
        lazyChunks = buildSpawnChunksBoundingBox(spawnX, spawnZ, 21, BoundingBoxType.LazySpawnChunks);
    }

    public void clearCache() {
        worldSpawn = null;
        spawnChunks = null;
        lazyChunks = null;
    }

    private static BoundingBoxWorldSpawn getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        Coords minCoords = new Coords(spawnX - 10, 0, spawnZ - 10);
        Coords maxCoords = new Coords(spawnX + 10, 0, spawnZ + 10);

        return new BoundingBoxWorldSpawn(minCoords, maxCoords, BoundingBoxType.WorldSpawn);
    }

    private static BoundingBoxWorldSpawn buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        int spawnChunkX = MathHelper.floor(spawnX / CHUNK_SIZE);
        int spawnChunkZ = MathHelper.floor(spawnZ / CHUNK_SIZE);
        int midOffset = ((size - 1) / 2);

        int minX = spawnChunkX - midOffset;
        int maxX = spawnChunkX + midOffset;
        int minZ = spawnChunkZ - midOffset;
        int maxZ = spawnChunkZ + midOffset;


        Coords maxCoords = new Coords(minX * CHUNK_SIZE, 0, minZ * CHUNK_SIZE);
        Coords minCoords = new Coords(16 + (maxX * CHUNK_SIZE), 0, 16 + (maxZ * CHUNK_SIZE));
        return new BoundingBoxWorldSpawn(minCoords, maxCoords, type);
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return dimensionId == DimensionId.OVERWORLD;
    }

    @Override
    public Iterable<BoundingBoxWorldSpawn> get(DimensionId dimensionId) {
        Set<BoundingBoxWorldSpawn> boundingBoxes = new HashSet<>();
        if (BoundingBoxTypeHelper.shouldRender(BoundingBoxType.WorldSpawn)) {
            if (worldSpawn != null) boundingBoxes.add(worldSpawn);
            if (spawnChunks != null) boundingBoxes.add(spawnChunks);
        }
        if (BoundingBoxTypeHelper.shouldRender(BoundingBoxType.LazySpawnChunks)) {
            if (lazyChunks != null) boundingBoxes.add(lazyChunks);
        }
        return boundingBoxes;
    }
}
