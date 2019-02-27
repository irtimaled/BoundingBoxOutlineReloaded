package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class ClientBoundingBoxProvider {
    private static final int CHUNK_SIZE = 16;
    private final ClientDimensionCache dimensionCache;

    ClientBoundingBoxProvider(ClientDimensionCache dimensionCache) {
        this.dimensionCache = dimensionCache;
    }

    private boolean isWithinRenderDistance(BlockPos minBlockPos, BlockPos maxBlockPos) {
        int renderDistanceBlocks = Minecraft.getInstance().gameSettings.renderDistanceChunks * CHUNK_SIZE;
        int minX = MathHelper.floor(PlayerData.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(PlayerData.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(PlayerData.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(PlayerData.getZ() + renderDistanceBlocks);

        return maxBlockPos.getX() > minX &&
                maxBlockPos.getZ() > minZ &&
                minBlockPos.getX() < maxX &&
                minBlockPos.getZ() < maxZ;
    }

    Set<BoundingBox> getBoundingBoxes(DimensionType dimensionType, Boolean outerBoxOnly) {
        Set<BoundingBox> boundingBoxes = getClientBoundingBoxes(dimensionType);
        BoundingBoxCache boundingBoxCache = dimensionCache.getBoundingBoxes(dimensionType);
        if (boundingBoxCache == null)
            return boundingBoxes;

        for (Map.Entry<BoundingBox, Set<BoundingBox>> entry : boundingBoxCache.getBoundingBoxes().entrySet()) {
            BoundingBox bb = entry.getKey();
            if (!isWithinRenderDistance(bb.getMinBlockPos(), bb.getMaxBlockPos())) continue;
            if (outerBoxOnly)
                boundingBoxes.add(bb);
            else
                boundingBoxes.addAll(entry.getValue());
        }

        return boundingBoxes;
    }

    private void addIfWithinRenderDistance(Set<BoundingBox> boundingBoxes, BoundingBox boundingBox) {
        if (isWithinRenderDistance(boundingBox.getMinBlockPos(), boundingBox.getMaxBlockPos()))
            boundingBoxes.add(boundingBox);
    }

    private Set<BoundingBox> getClientBoundingBoxes(DimensionType dimensionType) {
        WorldData worldData = dimensionCache.getWorldData();

        Set<BoundingBox> boundingBoxes = new HashSet<>();
        if (worldData != null && dimensionType == DimensionType.OVERWORLD) {
            if (ConfigManager.drawWorldSpawn.getBoolean()) {
                addIfWithinRenderDistance(boundingBoxes, getSpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
                addIfWithinRenderDistance(boundingBoxes, getWorldSpawnBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
            }
            if (ConfigManager.drawLazySpawnChunks.getBoolean()) {
                addIfWithinRenderDistance(boundingBoxes, getLazySpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
            }
            if (ConfigManager.drawSlimeChunks.getBoolean()) {
                boundingBoxes.addAll(this.getSlimeChunks());
            }
        }
        return boundingBoxes;
    }

    private Set<BoundingBoxSlimeChunk> getSlimeChunks() {
        int renderDistanceChunks = Minecraft.getInstance().gameSettings.renderDistanceChunks;
        int playerChunkX = MathHelper.floor(PlayerData.getX() / CHUNK_SIZE);
        int playerChunkZ = MathHelper.floor(PlayerData.getZ() / CHUNK_SIZE);
        Set<BoundingBoxSlimeChunk> slimeChunks = new HashSet<>();
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (!isSlimeChunk(chunkX, chunkZ)) continue;
                ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                BlockPos minBlockPos = new BlockPos(chunk.getXStart(), 1, chunk.getZStart());
                BlockPos maxBlockPos = new BlockPos(chunk.getXEnd(), 38, chunk.getZEnd());
                slimeChunks.add(BoundingBoxSlimeChunk.from(minBlockPos, maxBlockPos, Colors.DARK_GREEN));
            }
        }
        return slimeChunks;
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        WorldData worldData = dimensionCache.getWorldData();
        Random r = new Random(worldData.getSeed() +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    private BoundingBox getSpawnChunksBoundingBox(int spawnX, int spawnZ) {
        return dimensionCache.getOrSetSpawnChunks(() -> buildSpawnChunksBoundingBox(spawnX, spawnZ, 12));
    }

    private BoundingBox getLazySpawnChunksBoundingBox(int spawnX, int spawnZ) {
        return dimensionCache.getOrSetLazySpawnChunks(() -> buildSpawnChunksBoundingBox(spawnX, spawnZ, 16));
    }

    private BoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size) {
        double midOffset = CHUNK_SIZE * (size / 2.0);
        double midX = Math.round((float) (spawnX / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        double midZ = Math.round((float) (spawnZ / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        BlockPos minBlockPos = new BlockPos(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / (double) CHUNK_SIZE % 0.5D == 0.0D && spawnZ / (double) CHUNK_SIZE % 0.5D == 0.0D) {
            midX += (double) CHUNK_SIZE;
            midZ += (double) CHUNK_SIZE;
        }
        BlockPos maxBlockPos = new BlockPos(midX + midOffset, 0, midZ + midOffset);
        return BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, Color.RED);
    }

    private BoundingBox getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        return dimensionCache.getOrSetWorldSpawn(() -> buildWorldSpawnBoundingBox(spawnX, spawnZ));
    }

    private BoundingBox buildWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        BlockPos minBlockPos = new BlockPos(spawnX - 10, 0, spawnZ - 10);
        BlockPos maxBlockPos = new BlockPos(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minBlockPos, maxBlockPos, Color.RED);
    }
}
