package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.Dimensions;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashSet;
import java.util.Set;

public class WorldSpawnProvider implements IBoundingBoxProvider<BoundingBoxWorldSpawn> {
    private static final double CHUNK_SIZE = 16d;
    private static Set<BoundingBoxWorldSpawn> spawnChunks = new HashSet<>();

    public static void setWorldSpawn(int spawnX, int spawnZ) {
        spawnChunks = getSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    public static void clear() {
        spawnChunks = new HashSet<>();
    }

    private static Set<BoundingBoxWorldSpawn> getSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        Set<BoundingBoxWorldSpawn> boundingBoxes = new HashSet<>();
        boundingBoxes.add(getWorldSpawnBoundingBox(spawnX, spawnZ));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks));
        return boundingBoxes;
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

    public Iterable<BoundingBoxWorldSpawn> get(int dimensionId) {
        return dimensionId == Dimensions.OVERWORLD ? spawnChunks : Iterators.empty();
    }
}
