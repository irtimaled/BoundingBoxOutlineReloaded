package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.Dimensions;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ClientRenderer {
    private static final int CHUNK_SIZE = 16;
    private static final Map<Class<? extends AbstractBoundingBox>, AbstractRenderer> boundingBoxRendererMap = new HashMap<>();

    private final GetCache getCache;
    private long seed;
    private Set<AbstractBoundingBox> spawnChunkBoundingBoxes = new HashSet<>();

    ClientRenderer(GetCache getCache) {
        this.getCache = getCache;
        boundingBoxRendererMap.put(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        boundingBoxRendererMap.put(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        boundingBoxRendererMap.put(BoundingBoxStructure.class, new StructureRenderer());
        boundingBoxRendererMap.put(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
    }

    private boolean isWithinRenderDistance(AbstractBoundingBox boundingBox) {
        Coords minCoords = boundingBox.getMinCoords();
        Coords maxCoords = boundingBox.getMaxCoords();
        int renderDistanceBlocks = getRenderDistanceChunks() * CHUNK_SIZE;
        int minX = MathHelper.floor(PlayerCoords.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(PlayerCoords.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(PlayerCoords.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(PlayerCoords.getZ() + renderDistanceBlocks);

        return maxCoords.getX() >= minX &&
                maxCoords.getZ() >= minZ &&
                minCoords.getX() <= maxX &&
                minCoords.getZ() <= maxZ;
    }

    public void render(int dimensionId, Boolean outerBoxesOnly) {
        Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxes = getBoundingBoxes(dimensionId);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.get()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }
        for (Map.Entry<AbstractBoundingBox, Set<AbstractBoundingBox>> entry : boundingBoxes.entrySet()) {
            AbstractBoundingBox key = entry.getKey();
            if (!key.shouldRender()) continue;

            AbstractRenderer renderer = boundingBoxRendererMap.get(key.getClass());
            if (renderer == null) continue;

            if (!outerBoxesOnly) {
                Set<AbstractBoundingBox> children = entry.getValue();
                if (children != null) {
                    children.forEach(renderer::render);
                    continue;
                }
            }
            renderer.render(key);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private Map<AbstractBoundingBox, Set<AbstractBoundingBox>> getBoundingBoxes(int dimensionId) {
        Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxes = new HashMap<>();
        if (dimensionId == Dimensions.OVERWORLD) {
            if (BoundingBoxType.SlimeChunks.shouldRender()) {
                addSlimeChunks(boundingBoxes);
            }

            for (AbstractBoundingBox boundingBox : spawnChunkBoundingBoxes) {
                if (boundingBox.shouldRender() && isWithinRenderDistance(boundingBox)) {
                    boundingBoxes.put(boundingBox, null);
                }
            }
        }

        BoundingBoxCache cache = getCache.apply(dimensionId);
        if (cache != null) {
            for (Map.Entry<AbstractBoundingBox, Set<AbstractBoundingBox>> entry : cache.getBoundingBoxes().entrySet()) {
                AbstractBoundingBox key = entry.getKey();
                if (key.shouldRender() && isWithinRenderDistance(key)) {
                    boundingBoxes.put(key, entry.getValue());
                }
            }
        }
        return boundingBoxes;
    }

    private void addSlimeChunks(Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxes) {
        int renderDistanceChunks = getRenderDistanceChunks();
        int playerChunkX = MathHelper.floor(PlayerCoords.getX() / 16.0D);
        int playerChunkZ = MathHelper.floor(PlayerCoords.getZ() / 16.0D);
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (isSlimeChunk(chunkX, chunkZ)) {
                    int chunkXStart = chunkX << 4;
                    int chunkZStart = chunkZ << 4;
                    Coords minCoords = new Coords(chunkXStart, 1, chunkZStart);
                    Coords maxCoords = new Coords(chunkXStart + 15, 38, chunkZStart + 15);
                    boundingBoxes.put(BoundingBoxSlimeChunk.from(minCoords, maxCoords), null);
                }
            }
        }
    }

    private int getRenderDistanceChunks() {
        return Minecraft.getInstance().gameSettings.renderDistanceChunks;
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    void setSeed(long seed) {
        this.seed = seed;
    }

    void setWorldSpawn(int spawnX, int spawnZ) {
        spawnChunkBoundingBoxes = getSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    private Set<AbstractBoundingBox> getSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
        boundingBoxes.add(getWorldSpawnBoundingBox(spawnX, spawnZ));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks));
        return boundingBoxes;
    }

    private AbstractBoundingBox getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        Coords minCoords = new Coords(spawnX - 10, 0, spawnZ - 10);
        Coords maxCoords = new Coords(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, BoundingBoxType.WorldSpawn);
    }

    private AbstractBoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        double midOffset = CHUNK_SIZE * (size / 2.0);
        double midX = Math.round((float) (spawnX / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        double midZ = Math.round((float) (spawnZ / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        Coords maxCoords = new Coords(midX + midOffset, 0, midZ + midOffset);
        if ((spawnX / (double) CHUNK_SIZE) % 1.0D == 0.5D) {
            midX -= (double) CHUNK_SIZE;
        }
        if ((spawnZ / (double) CHUNK_SIZE) % 1.0D == 0.5D) {
            midZ -= (double) CHUNK_SIZE;
        }
        Coords minCoords = new Coords(midX - midOffset, 0, midZ - midOffset);
        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, type);
    }

    void clear() {
        spawnChunkBoundingBoxes = new HashSet<>();
    }
}
