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
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;

import java.util.Comparator;

public class BiomeBorderProvider implements IBoundingBoxProvider<BoundingBoxBiomeBorder>, ICachingProvider {

    private static final ObjectLinkedOpenHashSet<ChunkSectionPos> queuedUpdateChunks = new ObjectLinkedOpenHashSet<>();

    public static boolean runQueuedTasks() {
        if (queuedUpdateChunks.isEmpty()) return false;
        while (true) {
            final ChunkSectionPos sectionPos;
            synchronized (queuedUpdateChunks) {
                sectionPos = queuedUpdateChunks.removeFirst();
            }
            if (sectionPos == null) break;

            final ClientWorld world = MinecraftClient.getInstance().world;
            ChunkPos pos = sectionPos.toChunkPos();

            if (!world.isChunkLoaded(pos.x, pos.z) ||
                    !world.isChunkLoaded(pos.x - 1, pos.z) ||
                    !world.isChunkLoaded(pos.x + 1, pos.z) ||
                    !world.isChunkLoaded(pos.x, pos.z - 1) ||
                    !world.isChunkLoaded(pos.x, pos.z + 1)) {
                continue;
            }

            final long key = sectionPos.asLong();
            final BiomeBorderChunkSection chunk = chunks.get(key);
            if (chunk == null) {
                try {
                    chunks.put(key, new BiomeBorderChunkSection(sectionPos.getSectionX(), sectionPos.getSectionY(), sectionPos.getSectionZ()));
                } catch (IllegalStateException e) {
                    if (e.getMessage().equals("Chunk not loaded")) continue;
                    else throw new RuntimeException(e);
                }
                hasUpdate = true;
            }
            // we don't expect updates
//            else {
//                chunk.findBoxesFromBlockState(sectionPos.getMinX(), sectionPos.getMinY(), sectionPos.getMinZ());
//            }
            break;
        }
        return true;
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
                for (int y = 0; y < 16; y++) {
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

            final BoundingBoxBiomeBorder box = new BoundingBoxBiomeBorder(
                    new Coords(x, y, z),
                    northBiomeId != currentBiomeId,
                    eastBiomeId != currentBiomeId,
                    southBiomeId != currentBiomeId,
                    westBiomeId != currentBiomeId,
                    upBiomeId != currentBiomeId,
                    downBiomeId != currentBiomeId,
                    currentBiomeId);
            return box.hasRender() ? box : null;
        }

        public BoundingBoxBiomeBorder[] getBlocks() {
            return boxes;
        }
    }

    {
//        EventBus.subscribe(ClientWorldUpdateTracker.ChunkLoadEvent.class, event -> {
//            queuedUpdateChunks.add(new ChunkPos(event.x(), event.z()));
//            queuedUpdateChunks.add(new ChunkPos(event.x() + 1, event.z()));
//            queuedUpdateChunks.add(new ChunkPos(event.x() - 1, event.z()));
//            queuedUpdateChunks.add(new ChunkPos(event.x(), event.z() + 1));
//            queuedUpdateChunks.add(new ChunkPos(event.x(), event.z() - 1));
//        });
        EventBus.subscribe(ClientWorldUpdateTracker.ChunkUnloadEvent.class, event -> {
            final ClientWorld world = MinecraftClient.getInstance().world;
            synchronized (queuedUpdateChunks) {
                for (int y = world.getBottomSectionCoord(); y < world.getTopSectionCoord(); y++) {
                    queuedUpdateChunks.remove(ChunkSectionPos.from(event.x(), y, event.z()));
                    chunks.remove(ChunkSectionPos.asLong(event.x(), y, event.z()));
                }
            }
        });
        EventBus.subscribe(ClientWorldUpdateTracker.WorldResetEvent.class, event -> {
            chunks.clear();
            synchronized (queuedUpdateChunks) {
                queuedUpdateChunks.clear();
            }
        });
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        final boolean shouldRender = BoundingBoxTypeHelper.shouldRender(BoundingBoxType.BiomeBorder);
        if (!shouldRender) {
            cleanup();
        }
        return shouldRender;
    }

    public void cleanup() {
        queuedUpdateChunks.clear();
        if (!used) return;
        doCleanup = true;
        try {
            boxes.clear();
            boxes.trim();
            bfsQueue.clear();
            bfsQueue.trim();
            bfsSearchedPos.clear();
            bfsSearchedPos.trim();
            lastSectionPos = Long.MAX_VALUE;
        } finally {
            doCleanup = false;
        }
    }

    private boolean used = false;
    private final ReferenceArrayList<BoundingBoxBiomeBorder> boxes = new ReferenceArrayList<>();
    private boolean doCleanup = false;
    private final LongArrayFIFOQueue bfsQueue = new LongArrayFIFOQueue();
    private final LongOpenHashSet bfsSearchedPos = new LongOpenHashSet() {
        @Override
        protected void rehash(int newN) {
            if (doCleanup || newN > n) {
                super.rehash(newN);
            }
        }
    };

    private static boolean hasUpdate = false;
    private long lastSectionPos = Long.MAX_VALUE;
    private boolean lastRenderOnlyCurrentBiome = false;
    private int lastRenderDistanceChunks = 0;

    @Override
    public Iterable<BoundingBoxBiomeBorder> get(DimensionId dimensionId) {
        int renderDistanceChunks = ConfigManager.biomeBordersRenderDistance.get();
        final boolean renderOnlyCurrentBiome = ConfigManager.renderOnlyCurrentBiome.get();
        final int x = (int) Player.getX();
        final int y = (int) Player.getY();
        final int z = (int) Player.getZ();
        int playerChunkX = ChunkSectionPos.getSectionCoord(x);
        int playerChunkY = ChunkSectionPos.getSectionCoord(y);
        int playerChunkZ = ChunkSectionPos.getSectionCoord(z);
        long currentSectionPos = ChunkSectionPos.asLong(playerChunkX, playerChunkY, playerChunkZ);

        used = true;

        ObjectArrayList<ChunkSectionPos> pendingPoses = new ObjectArrayList<>();
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; chunkX++) {
            for (int chunkY = playerChunkY - renderDistanceChunks; chunkY <= playerChunkY + renderDistanceChunks; chunkY++) {
                for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; chunkZ++) {
                    long key = ChunkSectionPos.asLong(chunkX, chunkY, chunkZ);
                    BiomeBorderChunkSection chunk = chunks.get(key);
                    if (chunk == null) {
                        final ChunkSectionPos pos = ChunkSectionPos.from(chunkX, chunkY, chunkZ);
                        boolean alreadyQueued;
                        synchronized (queuedUpdateChunks) {
                            alreadyQueued = queuedUpdateChunks.contains(pos);
                        }
                        if (!alreadyQueued) pendingPoses.add(pos);
                    }
                }
            }
        }
        if (!pendingPoses.isEmpty()) {
            ChunkSectionPos currentSectionPosObj = ChunkSectionPos.from(playerChunkX, playerChunkY, playerChunkZ);
            pendingPoses.unstableSort(Comparator.comparingInt(value -> value.getManhattanDistance(currentSectionPosObj)));
            synchronized (queuedUpdateChunks) {
                queuedUpdateChunks.addAll(pendingPoses);
            }
        }

        if (lastRenderOnlyCurrentBiome != renderOnlyCurrentBiome ||
                lastRenderDistanceChunks != renderDistanceChunks ||
                hasUpdate) {
            bfsQueue.clear();
            bfsSearchedPos.clear();
            boxes.clear();
        }
        lastRenderDistanceChunks = renderDistanceChunks;
        lastRenderOnlyCurrentBiome = renderOnlyCurrentBiome;

        if (renderOnlyCurrentBiome) {
            if (hasUpdate || lastSectionPos != currentSectionPos || !bfsSearchedPos.contains(BlockPos.asLong(x, y, z))) {
                ReferenceArrayList<Direction> allowedDirections = new ReferenceArrayList<>();
                boxes.clear();
                bfsQueue.clear();
                bfsSearchedPos.clear();
                bfsQueue.enqueue(BlockPos.asLong(x, y, z));
                bfsSearchedPos.add(BlockPos.asLong(x, y, z));
                while (!bfsQueue.isEmpty()) {
                    long pos = bfsQueue.dequeueLong();
                    int blockX = BlockPos.unpackLongX(pos);
                    int blockY = BlockPos.unpackLongY(pos);
                    int blockZ = BlockPos.unpackLongZ(pos);

                    final int chunkX = ChunkSectionPos.getSectionCoord(blockX);
                    final int chunkY = ChunkSectionPos.getSectionCoord(blockY);
                    final int chunkZ = ChunkSectionPos.getSectionCoord(blockZ);
                    if (Math.abs(chunkX - playerChunkX) > renderDistanceChunks || Math.abs(chunkY - playerChunkY) > renderDistanceChunks || Math.abs(chunkZ - playerChunkZ) > renderDistanceChunks) {
                        continue;
                    }

                    long key = ChunkSectionPos.asLong(chunkX, chunkY, chunkZ);
                    BiomeBorderChunkSection chunk = chunks.get(key);
                    if (chunk == null) continue;
                    final BoundingBoxBiomeBorder box = chunk.boxes[BiomeBorderChunkSection.getIndex(blockX, blockY, blockZ)];

                    allowedDirections.clear();
                    if (box != null) {
                        boxes.add(box);
                        if (!box.renderUp()) allowedDirections.add(Direction.UP);
                        if (!box.renderDown()) allowedDirections.add(Direction.DOWN);
                        if (!box.renderNorth()) allowedDirections.add(Direction.NORTH);
                        if (!box.renderSouth()) allowedDirections.add(Direction.SOUTH);
                        if (!box.renderWest()) allowedDirections.add(Direction.WEST);
                        if (!box.renderEast()) allowedDirections.add(Direction.EAST);
                    } else {
                        for (Direction value : Direction.values()) {
                            allowedDirections.add(value);
                        }
                    }

                    for (Direction direction : allowedDirections) {
                        int neighborX = blockX + direction.getOffsetX();
                        int neighborY = blockY + direction.getOffsetY();
                        int neighborZ = blockZ + direction.getOffsetZ();
                        long neighborPos = BlockPos.asLong(neighborX, neighborY, neighborZ);
                        if (!bfsSearchedPos.contains(neighborPos)) {
                            bfsSearchedPos.add(neighborPos);
                            bfsQueue.enqueue(neighborPos);
                        }
                    }
                }
            }
        } else {
            if (hasUpdate || lastSectionPos != currentSectionPos) {
                boxes.clear();
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
            }
        }

        lastSectionPos = currentSectionPos;
        hasUpdate = false;
        return boxes;
    }

    public static int pendingUpdates() {
        return queuedUpdateChunks.size();
    }

    public void clearCache() {
        chunks.clear();
        synchronized (queuedUpdateChunks) {
            queuedUpdateChunks.clear();
        }
    }

    public void tick() {

    }

}
