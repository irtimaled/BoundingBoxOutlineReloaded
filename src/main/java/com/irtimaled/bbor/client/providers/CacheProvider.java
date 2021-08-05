package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.GetCache;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CacheProvider implements IBoundingBoxProvider<AbstractBoundingBox> {
    private static final int CHUNK_SIZE = 16;

    private final GetCache getCache;

    public CacheProvider(GetCache getCache) {
        this.getCache = getCache;
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
        BoundingBoxCache cache = getCache.apply(dimensionId);
        if (cache != null) {
            for (Map.Entry<AbstractBoundingBox, Set<AbstractBoundingBox>> entry : cache.getBoundingBoxes().entrySet()) {
                AbstractBoundingBox key = entry.getKey();
                if (BoundingBoxTypeHelper.shouldRender(key.getType()) && isWithinRenderDistance(key)) {
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
        return boundingBoxes;
    }
}
