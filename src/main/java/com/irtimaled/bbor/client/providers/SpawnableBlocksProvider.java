package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.interop.SpawnableBlocksHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SpawnableBlocksProvider implements IBoundingBoxProvider<BoundingBoxSpawnableBlocks>, ICachingProvider {

    private static final Queue<Runnable> queuedTasksForLighting = new ConcurrentLinkedQueue<>();

    public static void runQueuedLightingTasks() {
        Runnable runnable;
        while ((runnable = queuedTasksForLighting.poll()) != null) {
            runnable.run();
        }
    }

    private final Long2ObjectMap<SpawnableBlocksChunk> chunks = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>());

    private static class SpawnableBlocksChunk {

        private static int getIndex(int x, int z) {
            return (z & 0b1111) << 4 | (x & 0b1111);
        }

        private final BoundingBoxSpawnableBlocks[] boxes = new BoundingBoxSpawnableBlocks[16 * 16];

        public SpawnableBlocksChunk(int chunkX, int chunkZ) {
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
            BoundingBoxSpawnableBlocks box = boxes[getIndex(x, z)];
            if (box == null) {
                boxes[getIndex(x, z)] = box = new BoundingBoxSpawnableBlocks(x, z);
            }
            final ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) return;
            final WorldChunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
            if (chunk == null) return;

            final int maxY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z) + 1;
            box.getBlockYs().clear();

            // I have no idea how to properly use isSpawnable so i copied this
            BlockState upperBlockState = world.getBlockState(new BlockPos(x, world.getBottomY(), z));
            for (int y = world.getBottomY() + 1; y < maxY; y++) {
                BlockState spawnBlockState = upperBlockState;
                BlockPos pos = new BlockPos(x, y, z);
                upperBlockState = world.getBlockState(pos);
                if (SpawnableBlocksHelper.isSpawnable(world, pos, spawnBlockState, upperBlockState)) {
                    box.getBlockYs().add(y);
                }
            }
        }

        public BoundingBoxSpawnableBlocks[] getBlocks() {
            return boxes;
        }
    }

    {
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
            queuedTasksForLighting.add(() -> {
                this.chunks.put(ChunkPos.toLong(event.x(), event.z()), new SpawnableBlocksChunk(event.x(), event.z()));
            });
        });
        EventBus.subscribe(ClientWorldUpdateTracker.LightingUpdateEvent.class, event -> {
           queuedTasksForLighting.add(() -> {
               final SpawnableBlocksChunk chunk = this.chunks.get(ChunkPos.toLong(event.x(), event.z()));
               if (chunk != null) {
                   chunk.findBoxesFromBlockState(event.x() << 4, event.z() << 4);
               }
            });
        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            queuedTasksForLighting.add(() -> {
                this.chunks.remove(ChunkPos.toLong(event.x(), event.z()));
            });
        });
        EventBus.subscribe(ClientWorldUpdateTracker.BlockChangeEvent.class, event -> {
            queuedTasksForLighting.add(() -> {
                SpawnableBlocksChunk chunk = this.chunks.get(ChunkPos.toLong(ChunkSectionPos.getSectionCoord(event.x()), ChunkSectionPos.getSectionCoord(event.z())));
                if (chunk != null) {
                    chunk.findBoxesFromBlockState((event.x() >> 4) << 4, (event.z() >> 4) << 4);
                }
            });
        });
    }
    public void clearCache() {
        chunks.clear();
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.SpawnableBlocks);
    }

    @Override
    public Iterable<BoundingBoxSpawnableBlocks> get(DimensionId dimensionId) {
        int renderDistanceChunks = ClientInterop.getRenderDistanceChunks();
        int playerChunkX = ChunkSectionPos.getSectionCoord(Player.getX());
        int playerChunkZ = ChunkSectionPos.getSectionCoord(Player.getZ());

        ArrayList<BoundingBoxSpawnableBlocks> boxes = new ArrayList<>();

        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                long key = ChunkPos.toLong(chunkX, chunkZ);
                SpawnableBlocksChunk chunk = chunks.get(key);
                if (chunk == null) continue;
                for (BoundingBoxSpawnableBlocks box : chunk.getBlocks()) {
                    if (box != null) {
                        boxes.add(box);
                    }
                }
            }
        }
        return boxes;
    }

}
