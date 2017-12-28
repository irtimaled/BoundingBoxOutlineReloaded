package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.models.BoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxSlimeChunk;
import com.irtimaled.bbor.common.models.BoundingBoxWorldSpawn;
import com.irtimaled.bbor.common.models.WorldData;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class ClientBoundingBoxProvider {
    private final ClientDimensionCache dimensionCache;

    ClientBoundingBoxProvider(ClientDimensionCache dimensionCache) {
        this.dimensionCache = dimensionCache;
    }

    Set<BoundingBox> getClientBoundingBoxes(DimensionType dimensionType) {
        WorldData worldData = dimensionCache.getWorldData();

        if (worldData == null) {
            return null;
        }

        Set<BoundingBox> boundingBoxes = new HashSet<>();
        if (dimensionType == DimensionType.OVERWORLD) {
            if (ConfigManager.drawWorldSpawn.getBoolean()) {
                boundingBoxes.add(getWorldSpawnBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
                boundingBoxes.add(buildSpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
            }
            if (ConfigManager.drawLazySpawnChunks.getBoolean()) {
                boundingBoxes.add(getLazySpawnChunksBoundingBox(worldData.getSpawnX(), worldData.getSpawnZ()));
            }
            if (ConfigManager.drawSlimeChunks.getBoolean()) {
                boundingBoxes.addAll(this.getSlimeChunks());
            }
        }
        return boundingBoxes;
    }

    private Set<BoundingBoxSlimeChunk> getSlimeChunks() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int renderDistanceChunks = minecraft.gameSettings.renderDistanceChunks;
        int playerChunkX = MathHelper.floor(minecraft.player.posX / 16.0D);
        int playerChunkZ = MathHelper.floor(minecraft.player.posZ / 16.0D);
        Set<BoundingBoxSlimeChunk> slimeChunks = new HashSet<>();
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (isSlimeChunk(chunkX, chunkZ)) {
                    ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                    BlockPos minBlockPos = new BlockPos(chunk.getXStart(), 1, chunk.getZStart());
                    BlockPos maxBlockPos = new BlockPos(chunk.getXEnd(), 38, chunk.getZEnd());
                    if (minecraft.world.isAreaLoaded(minBlockPos, maxBlockPos)) {
                        slimeChunks.add(BoundingBoxSlimeChunk.from(minBlockPos, maxBlockPos, Color.GREEN));
                    }
                }
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

    private BoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ) {
        return dimensionCache.getOrSetSpawnChunks(() -> buildSpawnChunksBoundingBox(spawnX, spawnZ, 12));
    }

    private BoundingBox getLazySpawnChunksBoundingBox(int spawnX, int spawnZ) {
        return dimensionCache.getOrSetLazySpawnChunks(() -> buildSpawnChunksBoundingBox(spawnX, spawnZ, 16));
    }

    private BoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size) {
        double chunkSize = 16;
        double midOffset = chunkSize * (size / 2);
        double midX = Math.round((float) (spawnX / chunkSize)) * chunkSize;
        double midZ = Math.round((float) (spawnZ / chunkSize)) * chunkSize;
        BlockPos minBlockPos = new BlockPos(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / chunkSize % 0.5D == 0.0D && spawnZ / chunkSize % 0.5D == 0.0D) {
            midX += chunkSize;
            midZ += chunkSize;
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
