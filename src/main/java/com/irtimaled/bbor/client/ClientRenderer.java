package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.irtimaled.bbor.client.Constants.CHUNK_SIZE;

public class ClientRenderer {
    private final GetCache getCache;
    private static final Map<Class<? extends BoundingBox>, Renderer> boundingBoxRendererMap = new HashMap<>();

    ClientRenderer(GetCache getCache) {
        this.getCache = getCache;
        boundingBoxRendererMap.put(BoundingBoxVillage.class, new VillageRenderer());
        boundingBoxRendererMap.put(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        boundingBoxRendererMap.put(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        boundingBoxRendererMap.put(BoundingBoxStructure.class, new StructureRenderer());
        boundingBoxRendererMap.put(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
    }

    private boolean isWithinRenderDistance(BlockPos minBlockPos, BlockPos maxBlockPos) {
        int renderDistanceBlocks = Minecraft.getInstance().gameSettings.renderDistanceChunks * CHUNK_SIZE;
        int minX = MathHelper.floor(PlayerData.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(PlayerData.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(PlayerData.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(PlayerData.getZ() + renderDistanceBlocks);

        return maxBlockPos.getX() >= minX &&
                maxBlockPos.getZ() >= minZ &&
                minBlockPos.getX() <= maxX &&
                minBlockPos.getZ() <= maxZ;
    }

    public void render(DimensionType dimensionType, Boolean outerBoxesOnly) {
        BoundingBoxCache cache = getCache.apply(dimensionType);
        if (cache == null) return;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.getBoolean()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }
        for (Map.Entry<BoundingBox, Set<BoundingBox>> entry : cache.getBoundingBoxes().entrySet()) {
            BoundingBox key = entry.getKey();
            if (!key.shouldRender() || !isWithinRenderDistance(key.getMinBlockPos(), key.getMaxBlockPos())) continue;

            Renderer renderer = boundingBoxRendererMap.get(key.getClass());
            if (renderer == null) continue;

            if (!outerBoxesOnly) {
                Set<BoundingBox> boundingBoxes = entry.getValue();
                if (boundingBoxes != null) {
                    boundingBoxes.forEach(renderer::render);
                    continue;
                }
            }
            renderer.render(key);
        }

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
