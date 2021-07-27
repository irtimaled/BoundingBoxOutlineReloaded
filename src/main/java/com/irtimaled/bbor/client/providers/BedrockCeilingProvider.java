package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.BedrockCeilingHelper;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.models.BoundingBoxBedrockCeiling;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BedrockCeilingProvider implements IBoundingBoxProvider<BoundingBoxBedrockCeiling>, ICachingProvider {
    private static final double CHUNK_SIZE = 16d;
    private static Long lastGameTime = null;
    private static final Map<String, BedrockChunk> chunks = new HashMap<>();

    private static class BedrockChunk {
        private final Set<BoundingBoxBedrockCeiling> boxes = new HashSet<>();

        public BedrockChunk(int chunkX, int chunkZ) {
            int chunkStartX = chunkX << 4;
            int chunkStartZ = chunkZ << 4;

            if (BedrockCeilingHelper.chunkLoaded(chunkX, chunkZ)) findBoxesFromBlockState(chunkStartX, chunkStartZ);
            else findBoxesFromRNG(chunkX, chunkZ, chunkStartX, chunkStartZ);
        }

        private void findBoxesFromBlockState(int chunkStartX, int chunkStartZ) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Coords coords = getCoordsFromBlockState(chunkStartX + x, chunkStartZ + z);
                    if (coords != null) {
                        boxes.add(new BoundingBoxBedrockCeiling(coords));
                    }
                }
            }
        }

        private Coords getCoordsFromBlockState(int x, int z) {
            Coords coords = null;
            for (int y = 127; y >= 123; y--) {
                if (BedrockCeilingHelper.isBedrock(x, y, z)) {
                    if (coords == null) {
                        coords = new Coords(x, y, z);
                    } else {
                        return null;
                    }
                }
            }
            return coords;
        }

        private void findBoxesFromRNG(int chunkX, int chunkZ, int chunkStartX, int chunkStartZ) {
            Random random = BedrockCeilingHelper.getRandomForChunk(chunkX, chunkZ);

            // preseed 16x16x3 calls to nextDouble
            for (int dummy = 0; dummy < 768; dummy++) {
                random.nextDouble();
            }
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    Coords coords = getBlocksFromRNG(random, chunkStartX + x, chunkStartZ + z);

                    if (coords != null) {
                        boxes.add(new BoundingBoxBedrockCeiling(coords));
                    }
                }
            }
        }

        private Coords getBlocksFromRNG(Random random, int x, int z) {
            int count = 0;
            for (int y = 127; y >= 123; y--) {
                if (y >= 127 - random.nextInt(5)) {
                    count++;
                }
            }
            for (int y = 4; y >= 0; y--) {
                random.nextInt(5);
            }
            return count == 1 ? new Coords(x, 127, z) : null;
        }

        public Collection<? extends BoundingBoxBedrockCeiling> getBlocks() {
            return boxes;
        }

        public void clear() {
            boxes.clear();
        }
    }

    public void clearCache() {
        chunks.values().forEach(BedrockChunk::clear);
        chunks.clear();
    }

    @Override
    public Iterable<BoundingBoxBedrockCeiling> get(DimensionId dimensionId) {
        boolean shouldRecalculate = shouldRecalculate();

        int renderDistanceChunks = ClientInterop.getRenderDistanceChunks() / 2;
        int playerChunkX = MathHelper.floor(Player.getX() / CHUNK_SIZE);
        int playerChunkZ = MathHelper.floor(Player.getZ() / CHUNK_SIZE);

        Set<BoundingBoxBedrockCeiling> boxes = new HashSet<>();

        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                String key = String.format("%d,%d", chunkX, chunkZ);
                if (shouldRecalculate || !chunks.containsKey(key)) {
                    chunks.put(key, new BedrockChunk(chunkX, chunkZ));
                }
                BedrockChunk chunk = chunks.get(key);
                boxes.addAll(chunk.getBlocks());
            }
        }
        return boxes;
    }

    public boolean shouldRecalculate() {
        long gameTime = ClientInterop.getGameTime();
        if (!((Long) gameTime).equals(lastGameTime) && gameTime % 2L == 0L) {
            lastGameTime = gameTime;
            return true;
        }
        return false;
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return dimensionId == DimensionId.NETHER && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.BedrockCeiling) && Player.getY() > 110;
    }
}
