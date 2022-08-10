package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.BedrockCeilingHelper;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxBedrockCeiling;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.ArrayList;

public class BedrockCeilingProvider implements IBoundingBoxProvider<BoundingBoxBedrockCeiling>, ICachingProvider {

    private final Long2ObjectMap<BedrockChunk> chunks = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>());

    private static class BedrockChunk {

        private static int getIndex(int x, int z) {
            return (z & 0b1111) << 4 | (x & 0b1111);
        }

        private final BoundingBoxBedrockCeiling[] boxes = new BoundingBoxBedrockCeiling[16 * 16];

        public BedrockChunk(int chunkX, int chunkZ) {
            int chunkStartX = chunkX << 4;
            int chunkStartZ = chunkZ << 4;

            findBoxesFromBlockState(chunkStartX, chunkStartZ);
        }

        private void findBoxesFromBlockState(int chunkStartX, int chunkStartZ) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    findBoxFromBlockState(chunkStartX + x, chunkStartZ + z);
                }
            }
        }

        private void findBoxFromBlockState(int x, int z) {
            Coords coords = getCoordsFromBlockState(x, z);
            if (coords != null) {
                boxes[getIndex(x, z)] = new BoundingBoxBedrockCeiling(coords);
            } else {
                boxes[getIndex(x, z)] = null;
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

        public BoundingBoxBedrockCeiling[] getBlocks() {
            return boxes;
        }
    }

    {
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
            this.chunks.put(ChunkPos.toLong(event.x(), event.z()), new BedrockChunk(event.x(), event.z()));
        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            this.chunks.remove(ChunkPos.toLong(event.x(), event.z()));
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockChangeEvent.class, event -> {
            if (event.y() >= 123) {
                BedrockChunk chunk = this.chunks.get(ChunkPos.toLong(ChunkSectionPos.getSectionCoord(event.x()), ChunkSectionPos.getSectionCoord(event.z())));
                if (chunk != null) {
                    chunk.findBoxFromBlockState(event.x(), event.z());
                }
            }
        });
    }

    public void clearCache() {
        chunks.clear();
    }

    @Override
    public Iterable<BoundingBoxBedrockCeiling> get(DimensionId dimensionId) {
        int renderDistanceChunks = ClientInterop.getRenderDistanceChunks();
        int playerChunkX = ChunkSectionPos.getSectionCoord(Player.getX());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(Player.getZ());

        ArrayList<BoundingBoxBedrockCeiling> boxes = new ArrayList<>();

        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                long key = ChunkPos.toLong(chunkX, chunkZ);
                BedrockChunk chunk = chunks.get(key);
                if (chunk == null) continue;
                for (BoundingBoxBedrockCeiling box : chunk.getBlocks()) {
                    if (box != null) {
                        boxes.add(box);
                    }
                }
            }
        }
        return boxes;
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return dimensionId == DimensionId.NETHER && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.BedrockCeiling) && Player.getY() > 110;
    }
}
