package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.Dimensions;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashSet;
import java.util.Set;

public class WorldSpawnProvider implements IBoundingBoxProvider<BoundingBoxWorldSpawn> {
    private static final double CHUNK_SIZE = 16d;
    private static BoundingBoxWorldSpawn spawnChunks;
    private static BoundingBoxWorldSpawn lazyChunks;
    private static BoundingBoxWorldSpawn worldSpawn;

    public static void setWorldSpawn(int spawnX, int spawnZ) {
        worldSpawn = getWorldSpawnBoundingBox(spawnX, spawnZ);
        spawnChunks = buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks);
        lazyChunks = buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks);
    }

    public static void clear() {
        worldSpawn = null;
        spawnChunks = null;
        lazyChunks = null;
    }

    private static BoundingBoxWorldSpawn getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        Coords minCoords = new Coords(spawnX - 10, 0, spawnZ - 10);
        Coords maxCoords = new Coords(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, BoundingBoxType.WorldSpawn);
    }

    private static BoundingBoxWorldSpawn buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        double midOffset = CHUNK_SIZE * (size / 2.0);
        double midX = Math.round((float) (spawnX / CHUNK_SIZE)) * CHUNK_SIZE;
        double midZ = Math.round((float) (spawnZ / CHUNK_SIZE)) * CHUNK_SIZE;
        Coords maxCoords = new Coords(midX + midOffset, 0, midZ + midOffset);
        if ((spawnX / CHUNK_SIZE) % 1.0D == 0.5D) {
            midX -= CHUNK_SIZE;
        }
        if ((spawnZ / CHUNK_SIZE) % 1.0D == 0.5D) {
            midZ -= CHUNK_SIZE;
        }
        Coords minCoords = new Coords(midX - midOffset, 0, midZ - midOffset);
        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, type);
    }

    @Override
    public boolean canProvide(int dimensionId) {
        return dimensionId == Dimensions.OVERWORLD;
    }

    @Override
    public Iterable<BoundingBoxWorldSpawn> get(int dimensionId) {
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
