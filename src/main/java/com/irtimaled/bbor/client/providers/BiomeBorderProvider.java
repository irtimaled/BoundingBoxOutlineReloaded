package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.BiomeBorderHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxBiomeBorder;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.ArrayList;

public class BiomeBorderProvider implements IBoundingBoxProvider<BoundingBoxBiomeBorder>, ICachingProvider {

    private static final ObjectLinkedOpenHashSet<ChunkPos> queuedUpdateChunks = new ObjectLinkedOpenHashSet<>();

    public static void runQueuedTasks() {
        if (queuedUpdateChunks.isEmpty()) return;
        ChunkPos pos = queuedUpdateChunks.removeFirst();
        final ClientWorld world = MinecraftClient.getInstance().world;
        for (int y = world.getBottomSectionCoord(); y < world.getTopSectionCoord(); y ++) {
            final long key = ChunkSectionPos.asLong(pos.x, y, pos.z);
            final BiomeBorderChunkSection chunk = chunks.get(key);
            if (chunk != null) {
                chunk.findBoxesFromBlockState(pos.getStartX(), y, pos.getStartZ());
            } else {
                chunks.put(key, new BiomeBorderChunkSection(pos.x, y, pos.z));
            }
        }
    }

    private static final Long2ObjectMap<BiomeBorderChunkSection> chunks = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>());

    private static class BiomeBorderChunkSection {

        private static int getIndex(int x, int y, int z) {
            return (y & 0b1111) << 8 | (z & 0b1111) << 4 | (x & 0b1111);
        }

        private final BoundingBoxBiomeBorder[] boxes = new BoundingBoxBiomeBorder[16 * 16 * 16];

        public BiomeBorderChunkSection(int chunkX, int sectionY, int chunkZ) {
            int chunkStartX = ChunkSectionPos.getBlockCoord(chunkX);
            int chunkStartY = ChunkSectionPos.getBlockCoord(sectionY);
            int chunkStartZ = ChunkSectionPos.getBlockCoord(chunkZ);

            findBoxesFromBlockState(chunkStartX, chunkStartY, chunkStartZ);
        }

        private void findBoxesFromBlockState(int chunkStartX, int chunkStartY, int chunkStartZ) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y ++) {
                    for (int z = 0; z < 16; z++) {
                        findBoxFromBlockState(chunkStartX + x, chunkStartY + y, chunkStartZ + z);
                    }
                }
            }
        }

        private void findBoxFromBlockState(int x, int y, int z) {
            boxes[getIndex(x, y, z)] = getBox(x, y, z);
        }

        private BoundingBoxBiomeBorder getBox(int x, int y, int z) {
            int currentBiomeId = BiomeBorderHelper.getBiomeId(x, y, z);
            // fetch neighbor ids
            int northBiomeId = BiomeBorderHelper.getBiomeId(x, y, z - 1);
            int southBiomeId = BiomeBorderHelper.getBiomeId(x, y, z + 1);
            int westBiomeId = BiomeBorderHelper.getBiomeId(x - 1, y, z);
            int eastBiomeId = BiomeBorderHelper.getBiomeId(x + 1, y, z);
            int upBiomeId = BiomeBorderHelper.getBiomeId(x, y + 1, z);
            int downBiomeId = BiomeBorderHelper.getBiomeId(x, y - 1, z);

            return new BoundingBoxBiomeBorder(
                    new Coords(x, y, z),
                    northBiomeId != currentBiomeId,
                    eastBiomeId != currentBiomeId,
                    southBiomeId != currentBiomeId,
                    westBiomeId != currentBiomeId,
                    upBiomeId != currentBiomeId,
                    downBiomeId != currentBiomeId
            );
        }

        public BoundingBoxBiomeBorder[] getBlocks() {
            return boxes;
        }
    }

    {
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
            queuedUpdateChunks.add(new ChunkPos(event.x(), event.z()));
        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            queuedUpdateChunks.remove(new ChunkPos(event.x(), event.z()));
            final ClientWorld world = MinecraftClient.getInstance().world;
            for (int y = world.getBottomSectionCoord(); y < world.getTopSectionCoord(); y ++) {
                chunks.remove(ChunkSectionPos.asLong(event.x(), y, event.z()));
            }
        });
        EventBus.subscribe(ClientWorldUpdateTracker.WorldResetEvent.class, event -> {
            chunks.clear();
            queuedUpdateChunks.clear();
        });
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.BiomeBorder);
    }

    @Override
    public Iterable<BoundingBoxBiomeBorder> get(DimensionId dimensionId) {
        int renderDistanceChunks = ConfigManager.biomeBordersRenderDistance.get();
        int playerChunkX = ChunkSectionPos.getSectionCoord(Player.getX());
        int playerChunkY = ChunkSectionPos.getSectionCoord(Player.getY());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(Player.getZ());

        ArrayList<BoundingBoxBiomeBorder> boxes = new ArrayList<>();

        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkY = playerChunkY - renderDistanceChunks; chunkY <= playerChunkY + renderDistanceChunks; chunkY++) {
                for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                    long key = ChunkSectionPos.asLong(chunkX, chunkY, chunkZ);
                    BiomeBorderChunkSection chunk = chunks.get(key);
                    if (chunk == null) continue;
                    for (BoundingBoxBiomeBorder box : chunk.getBlocks()) {
                        if (box != null) {
                            boxes.add(box);
                        }
                    }
                }
            }
        }
        return boxes;
    }

    public static int pendingUpdates() {
        return queuedUpdateChunks.size();
    }

    public void clearCache() {
        chunks.clear();
        queuedUpdateChunks.clear();
    }

}
