package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.BiomeBorderHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.interop.FlowerForestHelper;
import com.irtimaled.bbor.client.models.BoundingBoxFlowerForest;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;

public class FlowerForestProvider implements IBoundingBoxProvider<BoundingBoxFlowerForest>, ICachingProvider {
    public static final int FLOWER_FOREST_BIOME_ID = BuiltinRegistries.BIOME.getRawId(FlowerForestHelper.BIOME);

    private final Long2ObjectMap<FlowerForestChunk> chunks = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>());

    private static class FlowerForestChunk {

        private static int getIndex(int x, int z) {
            return (z & 0b1111) << 4 | (x & 0b1111);
        }

        private final BoundingBoxFlowerForest[] boxes = new BoundingBoxFlowerForest[16 * 16];

        public FlowerForestChunk(int chunkX, int chunkZ) {
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
            boxes[getIndex(x, z)] = getBox(x, z);
        }

        private BoundingBoxFlowerForest getBox(int x, int z) {
            int biomeId = BiomeBorderHelper.getBiomeId(x, 255, z);
            if (biomeId == FLOWER_FOREST_BIOME_ID) {
                int y = getMaxYForPos(x, 128, z);
                final Coords coords = new Coords(x, y + 1, z);
                return new BoundingBoxFlowerForest(coords, FlowerForestHelper.getFlowerColorAtPos(coords));
            } else {
                return null;
            }
        }

        public BoundingBoxFlowerForest[] getBlocks() {
            return boxes;
        }
    }

    {
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
            this.chunks.put(ChunkPos.toLong(event.x(), event.z()), new FlowerForestChunk(event.x(), event.z()));
        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            this.chunks.remove(ChunkPos.toLong(event.x(), event.z()));
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockChangeEvent.class, event -> {
            FlowerForestChunk chunk = this.chunks.get(ChunkPos.toLong(ChunkSectionPos.getSectionCoord(event.x()), ChunkSectionPos.getSectionCoord(event.z())));
            if (chunk != null) {
                final BoundingBoxFlowerForest box = chunk.boxes[FlowerForestChunk.getIndex(event.x(), event.z())];
                if (box == null || box.getCoords().getY() > event.y() - 5) {
                    chunk.findBoxFromBlockState(event.x(), event.z());
                }
            }
        });
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.FlowerForest);
    }

    @Override
    public Iterable<BoundingBoxFlowerForest> get(DimensionId dimensionId) {
        int renderDistanceChunks = ConfigManager.flowerForestsRenderDistance.get();
        int playerChunkX = ChunkSectionPos.getSectionCoord(Player.getX());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(Player.getZ());

        ArrayList<BoundingBoxFlowerForest> boxes = new ArrayList<>();

//        Integer renderDistance = ConfigManager.flowerForestsRenderDistance.get();
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                long key = ChunkPos.toLong(chunkX, chunkZ);
                FlowerForestChunk chunk = chunks.get(key);
                if (chunk == null) continue;
                for (BoundingBoxFlowerForest box : chunk.getBlocks()) {
                    if (box != null) {
                        boxes.add(box);
                    }
                }
            }
        }

        return boxes;
    }

    public void clearCache() {
        chunks.clear();
    }

    private static int getMaxYForPos(int x, int y, int z) {
        int topY = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
        if (topY == 0) topY = y; // heightmap appears to be broken
        while (topY > 0) {
            if (FlowerForestHelper.canGrowFlower(x, topY, z)) return topY;
            topY--;
        }
        return 0;
    }
}
