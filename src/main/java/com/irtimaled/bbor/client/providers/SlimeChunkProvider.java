package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import com.irtimaled.bbor.client.models.BoundingBoxSlimeChunk;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.random.ChunkRandom;

import java.util.Objects;
import java.util.stream.Collectors;

public class SlimeChunkProvider implements IBoundingBoxProvider<BoundingBoxSlimeChunk>, ICachingProvider {
    private static final double CHUNK_SIZE = 16d;

    private static volatile Long seed;

    public static void setSeed(long seed) {
        SlimeChunkProvider.seed = seed;
        recalculateSlimeChunks();
    }

    private static final Long2ObjectMap<BoundingBoxSlimeChunk> chunks = Long2ObjectMaps.synchronize(new Long2ObjectLinkedOpenHashMap<>(), SlimeChunkProvider.class);
    private static ObjectList<BoundingBoxSlimeChunk> boxesCopy = new ObjectArrayList<>();

    static {
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
            chunks.put(ChunkPos.toLong(event.x(), event.z()), getSlimeChunkBox(event.x(), event.z()));
            updateCopy();
        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            chunks.remove(ChunkPos.toLong(event.x(), event.z()));
            updateCopy();
        });
    }

    private static boolean isSlimeChunk(int chunkX, int chunkZ) {
        return seed != null && ChunkRandom.getSlimeRandom(chunkX, chunkZ, seed, 987234911L).nextInt(10) == 0;
    }

    private static void recalculateSlimeChunks() {
        synchronized (SlimeChunkProvider.class) {
            for (Long2ObjectMap.Entry<BoundingBoxSlimeChunk> entry : chunks.long2ObjectEntrySet()) {
                final int chunkX = ChunkPos.getPackedX(entry.getLongKey());
                final int chunkZ = ChunkPos.getPackedZ(entry.getLongKey());
                entry.setValue(getSlimeChunkBox(chunkX, chunkZ));
            }
        }
        updateCopy();
    }

    private static BoundingBoxSlimeChunk getSlimeChunkBox(int chunkX, int chunkZ) {
        if (isSlimeChunk(chunkX, chunkZ)) {
            final ClientWorld world = MinecraftClient.getInstance().world;
            final int minimumY;
            if (world != null) {
                minimumY = world.getDimension().getMinimumY();
            } else {
                minimumY = 0;
            }
            int chunkXStart = chunkX << 4;
            int chunkZStart = chunkZ << 4;
            Coords minCoords = new Coords(chunkXStart, minimumY + 1, chunkZStart);
            Coords maxCoords = new Coords(chunkXStart + 15, 38, chunkZStart + 15);
            return new BoundingBoxSlimeChunk(minCoords, maxCoords);
        } else {
            return null;
        }
    }

    private static void updateCopy() {
        synchronized (SlimeChunkProvider.class) {
            boxesCopy = chunks.values().stream().filter(Objects::nonNull).collect(Collectors.toCollection(ObjectArrayList::new));
        }
    }

    public void clearCache() {
        seed = null;
        chunks.clear();
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return dimensionId == DimensionId.OVERWORLD && seed != null && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.SlimeChunks);
    }

    @Override
    public Iterable<BoundingBoxSlimeChunk> get(DimensionId dimensionId) {
        return boxesCopy;
    }
}
