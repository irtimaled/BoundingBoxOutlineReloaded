package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.Dimensions;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashSet;
import java.util.Set;

public class WorldSpawnProvider implements IBoundingBoxProvider<BoundingBoxWorldSpawn> {
    private static final double CHUNK_SIZE = 16d;
    private static Set<BoundingBoxWorldSpawn> spawnChunks = new HashSet<>();

    public static void setWorldSpan(int spawnX, int spawnZ) {
        spawnChunks = getSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    public static void clear() {
        spawnChunks = new HashSet<>();
    }

    private static Set<BoundingBoxWorldSpawn> getSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        Set<BoundingBoxWorldSpawn> boundingBoxes = new HashSet<>();
        boundingBoxes.add(getWorldSpawnBoundingBox(spawnX, spawnZ));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 19, BoundingBoxType.SpawnChunks));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 21, BoundingBoxType.LazySpawnChunks));
        return boundingBoxes;
    }

    private static BoundingBoxWorldSpawn getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        Coords minCoords = new Coords(spawnX - 10, 0, spawnZ - 10);
        Coords maxCoords = new Coords(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, BoundingBoxType.WorldSpawn);
    }

    private static BoundingBoxWorldSpawn buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        int spawnChunkX = MathHelper.floor(spawnX / CHUNK_SIZE);
        int spawnChunkZ = MathHelper.floor(spawnZ / CHUNK_SIZE);
        int midOffset = ((size-1) / 2);

        int minX = spawnChunkX - midOffset;
        int maxX = spawnChunkX + midOffset;
        int minZ = spawnChunkZ - midOffset;
        int maxZ = spawnChunkZ + midOffset;


        Coords maxCoords = new Coords(minX * CHUNK_SIZE, 0, minZ * CHUNK_SIZE);
        Coords minCoords = new Coords(16 + (maxX * CHUNK_SIZE), 0, 16 + (maxZ * CHUNK_SIZE));
        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, type);
    }

    public Iterable<BoundingBoxWorldSpawn> get(int dimensionId) {
        return dimensionId == Dimensions.OVERWORLD ? spawnChunks : Iterators.empty();
    }
}
