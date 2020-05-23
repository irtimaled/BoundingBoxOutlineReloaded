package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.models.BoundingBoxSlimeChunk;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SlimeChunkProvider implements IBoundingBoxProvider<BoundingBoxSlimeChunk> {
    private static final double CHUNK_SIZE = 16d;

    private static Long seed;

    public static void setSeed(long seed) {
        SlimeChunkProvider.seed = seed;
    }

    private static boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    public static void clear() {
        seed = null;
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return dimensionId == DimensionId.OVERWORLD && seed != null && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.SlimeChunks);
    }

    @Override
    public Iterable<BoundingBoxSlimeChunk> get(DimensionId dimensionId) {
        Set<BoundingBoxSlimeChunk> slimeChunks = new HashSet<>();
        int renderDistanceChunks = ClientInterop.getRenderDistanceChunks();
        int playerChunkX = MathHelper.floor(Player.getX() / CHUNK_SIZE);
        int playerChunkZ = MathHelper.floor(Player.getZ() / CHUNK_SIZE);
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (isSlimeChunk(chunkX, chunkZ)) {
                    int chunkXStart = chunkX << 4;
                    int chunkZStart = chunkZ << 4;
                    Coords minCoords = new Coords(chunkXStart, 1, chunkZStart);
                    Coords maxCoords = new Coords(chunkXStart + 15, 38, chunkZStart + 15);
                    slimeChunks.add(BoundingBoxSlimeChunk.from(minCoords, maxCoords));
                }
            }
        }
        return slimeChunks;
    }
}
