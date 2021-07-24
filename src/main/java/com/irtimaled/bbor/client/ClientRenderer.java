package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.models.*;
import com.irtimaled.bbor.client.providers.*;
import com.irtimaled.bbor.client.renderers.*;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.stream.Stream;

public class ClientRenderer {
    private static final int CHUNK_SIZE = 16;
    private static final Map<Class<? extends AbstractBoundingBox>, AbstractRenderer> boundingBoxRendererMap = new HashMap<>();

    private static boolean active;
    private static final Set<IBoundingBoxProvider> providers = new HashSet<>();

    public static boolean getActive() {
        return active;
    }

    public static void toggleActive() {
        active = !active;
        if (!active) return;

        Player.setActiveY();
    }

    static void deactivate() {
        active = false;
    }

    static {
        registerRenderer(BoundingBoxSlimeChunk.class, new SlimeChunkRenderer());
        registerRenderer(BoundingBoxWorldSpawn.class, new WorldSpawnRenderer());
        registerRenderer(BoundingBoxCuboid.class, new CuboidRenderer());
        registerRenderer(BoundingBoxMobSpawner.class, new MobSpawnerRenderer());
        registerRenderer(BoundingBoxSpawningSphere.class, new SpawningSphereRenderer());
        registerRenderer(BoundingBoxBeacon.class, new BeaconRenderer());
        registerRenderer(BoundingBoxBiomeBorder.class, new BiomeBorderRenderer());
        registerRenderer(BoundingBoxConduit.class, new ConduitRenderer());
        registerRenderer(BoundingBoxSpawnableBlocks.class, new SpawnableBlocksRenderer());
        registerRenderer(BoundingBoxLine.class, new LineRenderer());
        registerRenderer(BoundingBoxSphere.class, new SphereRenderer());
        registerRenderer(BoundingBoxFlowerForest.class, new FlowerForestRenderer());
        registerRenderer(BoundingBoxBedrockCeiling.class, new CuboidRenderer());

        registerProvider(new SlimeChunkProvider());
        registerProvider(new WorldSpawnProvider());
        registerProvider(new SpawningSphereProvider());
        registerProvider(new BeaconProvider());
        registerProvider(new CustomBoxProvider());
        registerProvider(new CustomBeaconProvider());
        registerProvider(new BiomeBorderProvider());
        registerProvider(new MobSpawnerProvider());
        registerProvider(new ConduitProvider());
        registerProvider(new SpawnableBlocksProvider());
        registerProvider(new CustomLineProvider());
        registerProvider(new CustomSphereProvider());
        registerProvider(new FlowerForestProvider());
        registerProvider(new BedrockCeilingProvider());
    }

    public static <T extends AbstractBoundingBox> void registerProvider(IBoundingBoxProvider<T> provider) {
        providers.add(provider);
    }

    public static <T extends AbstractBoundingBox> void registerRenderer(Class<? extends T> type, AbstractRenderer<T> renderer) {
        boundingBoxRendererMap.put(type, renderer);
    }

    private static boolean isWithinRenderDistance(AbstractBoundingBox boundingBox) {
        int renderDistanceBlocks = ClientInterop.getRenderDistanceChunks() * CHUNK_SIZE;
        int minX = MathHelper.floor(Player.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(Player.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(Player.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(Player.getZ() + renderDistanceBlocks);

        return boundingBox.intersectsBounds(minX, minZ, maxX, maxZ);
    }

    public static void render(MatrixStack matrixStack, DimensionId dimensionId) {
        if (!active) return;

        RenderHelper.beforeRender();

        getBoundingBoxes(dimensionId).forEach(key -> {
            AbstractRenderer renderer = boundingBoxRendererMap.get(key.getClass());
            if (renderer != null) renderer.render(matrixStack, key);
        });

        RenderHelper.afterRender();
    }

    public static void renderDeferred() {
        RenderHelper.beforeRender();
        RenderHelper.polygonModeFill();
        RenderHelper.enableBlend();
        RenderQueue.renderDeferred();
        RenderHelper.disableBlend();
        RenderHelper.enablePolygonOffsetLine();
        RenderHelper.polygonOffsetMinusOne();
        RenderHelper.afterRender();
    }

    public static Stream<AbstractBoundingBox> getBoundingBoxes(DimensionId dimensionId) {
        Stream.Builder<AbstractBoundingBox> boundingBoxes = Stream.builder();
        for (IBoundingBoxProvider<?> provider : providers) {
            if (provider.canProvide(dimensionId)) {
                for (AbstractBoundingBox boundingBox : provider.get(dimensionId)) {
                    if (isWithinRenderDistance(boundingBox)) {
                        boundingBoxes.accept(boundingBox);
                    }
                }
            }
        }

        Point point = Player.getPoint();
        return boundingBoxes.build()
                .sorted(Comparator
                        .comparingDouble((AbstractBoundingBox boundingBox) -> boundingBox.getDistance(point.getX(), point.getY(), point.getZ())).reversed());
    }

    public static void clear() {
        for(IBoundingBoxProvider<?> provider : providers) {
            TypeHelper.doIfType(provider, ICachingProvider.class, ICachingProvider::clearCache);
        }
    }
}
