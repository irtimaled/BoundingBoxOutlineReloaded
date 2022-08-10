package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.client.providers.BeaconProvider;
import com.irtimaled.bbor.client.providers.BedrockCeilingProvider;
import com.irtimaled.bbor.client.providers.ConduitProvider;
import com.irtimaled.bbor.client.providers.CustomBeaconProvider;
import com.irtimaled.bbor.client.providers.CustomBoxProvider;
import com.irtimaled.bbor.client.providers.CustomLineProvider;
import com.irtimaled.bbor.client.providers.CustomSphereProvider;
import com.irtimaled.bbor.client.providers.FlowerForestProvider;
import com.irtimaled.bbor.client.providers.IBoundingBoxProvider;
import com.irtimaled.bbor.client.providers.ICachingProvider;
import com.irtimaled.bbor.client.providers.MobSpawnerProvider;
import com.irtimaled.bbor.client.providers.SlimeChunkProvider;
import com.irtimaled.bbor.client.providers.SpawnableBlocksProvider;
import com.irtimaled.bbor.client.providers.SpawningSphereProvider;
import com.irtimaled.bbor.client.providers.WorldSpawnProvider;
import com.irtimaled.bbor.client.renderers.AbstractRenderer;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ClientRenderer {
    private static final int CHUNK_SIZE = 16;
    private static final Map<Class<? extends AbstractBoundingBox>, AbstractRenderer> boundingBoxRendererMap = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

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
        registerProvider(new SlimeChunkProvider());
        registerProvider(new WorldSpawnProvider());
        registerProvider(new SpawningSphereProvider());
        registerProvider(new BeaconProvider());
        registerProvider(new CustomBoxProvider());
        registerProvider(new CustomBeaconProvider());
//        registerProvider(new BiomeBorderProvider());
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

    public static <T extends AbstractBoundingBox> AbstractRenderer<T> registerRenderer(Class<? extends T> type, Supplier<AbstractRenderer<T>> renderer) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return null;
        final AbstractRenderer<T> renderer1 = renderer.get();
        boundingBoxRendererMap.put(type, renderer1);
        return renderer1;
    }

    public static AbstractRenderer getRenderer(Class<? extends AbstractBoundingBox> clazz) {
        return boundingBoxRendererMap.get(clazz);
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

        AsyncRenderer.render(matrixStack, dimensionId);

    }

    private static final ObjectArrayList<AbstractBoundingBox> listForRendering = new ObjectArrayList<>();

    public static List<AbstractBoundingBox> getBoundingBoxes(DimensionId dimensionId) {
        listForRendering.clear();
        final boolean doPreCulling = !ConfigManager.asyncBuilding.get() && ConfigManager.fastRender.get() >= 2;
        for (IBoundingBoxProvider<?> provider : providers) {
            if (provider.canProvide(dimensionId)) {
                for (AbstractBoundingBox boundingBox : provider.get(dimensionId)) {
                    if (isWithinRenderDistance(boundingBox)) {
                        if (doPreCulling && !boundingBox.isVisibleCulling()) continue;
                        listForRendering.add(boundingBox);
                    }
                }
            }
        }

        Point point = Player.getPoint();
        listForRendering.sort(Comparator.comparingDouble((AbstractBoundingBox boundingBox) -> boundingBox.getDistance(point.getX(), point.getY(), point.getZ())).reversed());

        return listForRendering;
    }

    public static void clear() {
        for (IBoundingBoxProvider<?> provider : providers) {
            TypeHelper.doIfType(provider, ICachingProvider.class, ICachingProvider::clearCache);
        }
    }

}
