package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.providers.IBoundingBoxProvider;
import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.*;
import com.irtimaled.bbor.config.ConfigManager;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientRenderer {
    private static final int CHUNK_SIZE = 16;
    private static final Map<Class<? extends AbstractBoundingBox>, AbstractRenderer> boundingBoxRendererMap = new HashMap<>();

    private static boolean active;
    private Set<IBoundingBoxProvider> providers = new HashSet<>();

    public static boolean getActive() {
        return active;
    }

    public static void toggleActive() {
        active = !active;
        if (!active) return;

        PlayerCoords.setActiveY();
    }

    static void deactivate() {
        active = false;
    }

    private final GetCache getCache;

    ClientRenderer(GetCache getCache) {
        this.getCache = getCache;
        registerRenderer(BoundingBoxVillage.class, new VillageRenderer());
        registerRenderer(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        registerRenderer(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        registerRenderer(BoundingBoxCuboid.class, new CuboidRenderer());
        registerRenderer(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
        registerRenderer(BoundingBoxSpawningSphere.class, new SpawningSphereRenderer());
        registerRenderer(BoundingBoxBeacon.class, new CuboidRenderer());
    }

    public <T extends AbstractBoundingBox> ClientRenderer registerProvider(IBoundingBoxProvider<T> provider) {
        this.providers.add(provider);
        return this;
    }

    public <T extends AbstractBoundingBox> ClientRenderer registerRenderer(Class<? extends T> type, AbstractRenderer<T> renderer) {
        boundingBoxRendererMap.put(type, renderer);
        return this;
    }

    private boolean isWithinRenderDistance(AbstractBoundingBox boundingBox) {
        int renderDistanceBlocks = ClientInterop.getRenderDistanceChunks() * CHUNK_SIZE;
        int minX = MathHelper.floor(PlayerCoords.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(PlayerCoords.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(PlayerCoords.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(PlayerCoords.getZ() + renderDistanceBlocks);

        return boundingBox.intersectsBounds(minX, minZ, maxX, maxZ);
    }

    public void render(int dimensionId) {
        if(!active) return;

        Map<AbstractBoundingBox, Set<AbstractBoundingBox>> boundingBoxes = getBoundingBoxes(dimensionId);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (ConfigManager.alwaysVisible.get()) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        }

        Boolean outerBoxesOnly = ConfigManager.outerBoxesOnly.get();
        for (Map.Entry<AbstractBoundingBox, Set<AbstractBoundingBox>> entry : boundingBoxes.entrySet()) {
            AbstractBoundingBox key = entry.getKey();
            if (!key.shouldRender()) continue;

            AbstractRenderer renderer = boundingBoxRendererMap.get(key.getClass());
            if (renderer == null) continue;

            if (!outerBoxesOnly) {
                Set<AbstractBoundingBox> children = entry.getValue();
                if (children != null && children.size() > 0) {
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
        for(IBoundingBoxProvider<?> provider: providers) {
            for (AbstractBoundingBox boundingBox : provider.get(dimensionId)) {
                boundingBoxes.put(boundingBox, null);
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
}
