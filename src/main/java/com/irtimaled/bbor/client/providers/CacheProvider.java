package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.GetCache;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.config.Setting;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CacheProvider implements IBoundingBoxProvider<AbstractBoundingBox> {
    private static final int CHUNK_SIZE = 16;

    private final GetCache getCache;
    private final Setting<Boolean>[] shouldRender;

    public CacheProvider(GetCache getCache) {
        this.getCache = getCache;
        this.shouldRender = new Setting[BoundingBoxCache.Type.values().length];
        for (BoundingBoxCache.Type type : BoundingBoxCache.Type.values()) {
            this.shouldRender[type.ordinal()] = ConfigManager.receivedTypeShouldRender(type);
        }
    }

    private static boolean isWithinRenderDistance(AbstractBoundingBox boundingBox) {
        int renderDistanceBlocks = ClientInterop.getRenderDistanceChunks() * CHUNK_SIZE;
        int minX = MathHelper.floor(Player.getX() - renderDistanceBlocks);
        int maxX = MathHelper.floor(Player.getX() + renderDistanceBlocks);
        int minZ = MathHelper.floor(Player.getZ() - renderDistanceBlocks);
        int maxZ = MathHelper.floor(Player.getZ() + renderDistanceBlocks);

        return boundingBox.intersectsBounds(minX, minZ, maxX, maxZ);
    }

    @Override
    public Iterable<AbstractBoundingBox> get(DimensionId dimensionId) {
        Boolean outerBoxesOnly = ConfigManager.outerBoxesOnly.get();

        List<AbstractBoundingBox> boundingBoxes = new ArrayList<>();
        if (ConfigManager.autoSelectReceivedType.get()) {
            final boolean hasLocalRender = renderType(dimensionId, outerBoxesOnly, boundingBoxes, BoundingBoxCache.Type.LOCAL);
            if (!(MinecraftClient.getInstance().isIntegratedServerRunning() && hasLocalRender)) {
                for (BoundingBoxCache.Type value : BoundingBoxCache.Type.values()) {
                    if (value == BoundingBoxCache.Type.LOCAL) continue;
                    if (renderType(dimensionId, outerBoxesOnly, boundingBoxes, value)) break;
                }
            }
        } else {
            for (BoundingBoxCache.Type value : BoundingBoxCache.Type.values()) {
                if (shouldRender[value.ordinal()].get()) renderType(dimensionId, outerBoxesOnly, boundingBoxes, value);
            }
        }
        return boundingBoxes;
    }

    private boolean renderType(DimensionId dimensionId, Boolean outerBoxesOnly, List<AbstractBoundingBox> boundingBoxes, BoundingBoxCache.Type type) {
        boolean hasWork = false;
        BoundingBoxCache cache = getCache.apply(type, dimensionId);
        if (cache != null) {
            for (Map.Entry<AbstractBoundingBox, Set<AbstractBoundingBox>> entry : cache.getBoundingBoxes().entrySet()) {
                AbstractBoundingBox key = entry.getKey();
                if (BoundingBoxTypeHelper.shouldRender(key.getType()) && isWithinRenderDistance(key)) {
                    hasWork = true;
                    if (!outerBoxesOnly) {
                        Set<AbstractBoundingBox> children = entry.getValue();
                        if (children != null && children.size() > 0) {
                            boundingBoxes.addAll(children);
                            continue;
                        }
                    }
                    boundingBoxes.add(key);
                }
            }
        }
        return hasWork;
    }
}
