package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static com.irtimaled.bbor.client.Constants.CHUNK_SIZE;

public class ClientRenderer {
    private final GetCache getCache;
    private static final Map<Class<? extends BoundingBox>, Renderer> boundingBoxRendererMap = new HashMap<>();
    private long seed;
    private Set<BoundingBox> spawnChunkBoundingBoxes = new HashSet<>();

    ClientRenderer(GetCache getCache) {
        this.getCache = getCache;
        boundingBoxRendererMap.put(BoundingBoxVillage.class, new VillageRenderer());
        boundingBoxRendererMap.put(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        boundingBoxRendererMap.put(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        boundingBoxRendererMap.put(BoundingBoxStructure.class, new StructureRenderer());
        boundingBoxRendererMap.put(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
    }

    private boolean isWithinRenderDistance(Coords minCoords, Coords maxCoords) {
        int renderDistanceBlocks = Minecraft.getInstance().gameSettings.renderDistanceChunks * CHUNK_SIZE;
        int minX = MathHelper.floor(PlayerCoords.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(PlayerCoords.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(PlayerCoords.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(PlayerCoords.getZ() + renderDistanceBlocks);

        return maxCoords.getX() >= minX &&
                maxCoords.getZ() >= minZ &&
                minCoords.getX() <= maxX &&
                minCoords.getZ() <= maxZ;
    }

    private boolean isWithinRenderDistance(BoundingBox boundingBox) {
        return isWithinRenderDistance(boundingBox.getMinCoords(), boundingBox.getMaxCoords());
    }

    public void render(DimensionType dimensionType, Boolean outerBoxesOnly) {
        Map<BoundingBox, Set<BoundingBox>> boundingBoxes = getBoundingBoxes(dimensionType);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.get()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }
        for (Map.Entry<BoundingBox, Set<BoundingBox>> entry : boundingBoxes.entrySet()) {
            BoundingBox key = entry.getKey();
            if (!key.shouldRender()) continue;

            Renderer renderer = boundingBoxRendererMap.get(key.getClass());
            if (renderer == null) continue;

            if (!outerBoxesOnly) {
                Set<BoundingBox> children = entry.getValue();
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

    private Map<BoundingBox, Set<BoundingBox>> getBoundingBoxes(DimensionType dimensionType) {
        Map<BoundingBox, Set<BoundingBox>> boundingBoxes = new HashMap<>();
        if (dimensionType == DimensionType.OVERWORLD) {
            if (BoundingBoxType.SlimeChunks.shouldRender()) {
                addSlimeChunks(boundingBoxes);
            }

            for (BoundingBox boundingBox : spawnChunkBoundingBoxes) {
                if (boundingBox.shouldRender() && isWithinRenderDistance(boundingBox)) {
                    boundingBoxes.put(boundingBox, null);
                }
            }
        }

        BoundingBoxCache cache = getCache.apply(dimensionType);
        if (cache != null) {
            for (Map.Entry<BoundingBox, Set<BoundingBox>> entry : cache.getBoundingBoxes().entrySet()) {
                BoundingBox key = entry.getKey();
                if (key.shouldRender() && isWithinRenderDistance(key)) {
                    boundingBoxes.put(key, entry.getValue());
                }
            }
        }
        return boundingBoxes;
    }

    private void addSlimeChunks(Map<BoundingBox, Set<BoundingBox>> boundingBoxes) {
        Minecraft minecraft = Minecraft.getInstance();
        int renderDistanceChunks = minecraft.gameSettings.renderDistanceChunks;
        int playerChunkX = MathHelper.floor(PlayerCoords.getX() / 16.0D);
        int playerChunkZ = MathHelper.floor(PlayerCoords.getZ() / 16.0D);
        for (int chunkX = playerChunkX - renderDistanceChunks; chunkX <= playerChunkX + renderDistanceChunks; ++chunkX) {
            for (int chunkZ = playerChunkZ - renderDistanceChunks; chunkZ <= playerChunkZ + renderDistanceChunks; ++chunkZ) {
                if (isSlimeChunk(chunkX, chunkZ)) {
                    ChunkPos chunk = new ChunkPos(chunkX, chunkZ);
                    Coords minCoords = new Coords(chunk.getXStart(), 1, chunk.getZStart());
                    Coords maxCoords = new Coords(chunk.getXEnd(), 38, chunk.getZEnd());
                    boundingBoxes.put(BoundingBoxSlimeChunk.from(minCoords, maxCoords), null);
                }
            }
        }
    }

    private boolean isSlimeChunk(int chunkX, int chunkZ) {
        Random r = new Random(seed +
                (long) (chunkX * chunkX * 4987142) +
                (long) (chunkX * 5947611) +
                (long) (chunkZ * chunkZ) * 4392871L +
                (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }

    void setWorldData(long seed, int spawnX, int spawnZ) {
        this.seed = seed;
        spawnChunkBoundingBoxes = getSpawnChunkBoundingBoxes(spawnX, spawnZ);
    }

    private Set<BoundingBox> getSpawnChunkBoundingBoxes(int spawnX, int spawnZ) {
        Set<BoundingBox> boundingBoxes = new HashSet<>();
        boundingBoxes.add(getWorldSpawnBoundingBox(spawnX, spawnZ));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 12, BoundingBoxType.SpawnChunks));
        boundingBoxes.add(buildSpawnChunksBoundingBox(spawnX, spawnZ, 16, BoundingBoxType.LazySpawnChunks));
        return boundingBoxes;
    }

    private BoundingBox getWorldSpawnBoundingBox(int spawnX, int spawnZ) {
        Coords minCoords = new Coords(spawnX - 10, 0, spawnZ - 10);
        Coords maxCoords = new Coords(spawnX + 10, 0, spawnZ + 10);

        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, BoundingBoxType.WorldSpawn);
    }

    private BoundingBox buildSpawnChunksBoundingBox(int spawnX, int spawnZ, int size, BoundingBoxType type) {
        double midOffset = CHUNK_SIZE * (size / 2.0);
        double midX = Math.round((float) (spawnX / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        double midZ = Math.round((float) (spawnZ / (double) CHUNK_SIZE)) * (double) CHUNK_SIZE;
        Coords minCoords = new Coords(midX - midOffset, 0, midZ - midOffset);
        if (spawnX / (double) CHUNK_SIZE % 0.5D == 0.0D && spawnZ / (double) CHUNK_SIZE % 0.5D == 0.0D) {
            midX += (double) CHUNK_SIZE;
            midZ += (double) CHUNK_SIZE;
        }
        Coords maxCoords = new Coords(midX + midOffset, 0, midZ + midOffset);
        return BoundingBoxWorldSpawn.from(minCoords, maxCoords, type);
    }
}
