package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.SpawnableBlocksHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawnableBlocks;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;

import java.util.Collections;
import java.util.Set;

public class SpawnableBlocksProvider implements IBoundingBoxProvider<BoundingBoxSpawnableBlocks>, ICachingProvider {
    public static final MinecraftClient minecraft = MinecraftClient.getInstance();
    private static Long lastGameTime = null;

    private static Set<BoundingBoxSpawnableBlocks> lastBoundingBox = null;

    public void clearCache() {
        lastGameTime = null;
        lastBoundingBox = null;
    }

    private boolean isWithinActiveSpawningSphere() {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.AFKSphere) &&
                ConfigManager.renderAFKSpawnableBlocks.get() &&
                SpawningSphereProvider.playerInsideSphere();
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return BoundingBoxTypeHelper.shouldRender(BoundingBoxType.SpawnableBlocks) &&
                !isWithinActiveSpawningSphere();
    }

    @Override
    public Iterable<BoundingBoxSpawnableBlocks> get(DimensionId dimensionId) {
        long gameTime = minecraft.world.getTime();
        if (lastBoundingBox == null || (!((Long) gameTime).equals(lastGameTime) && gameTime % 2L == 0L)) {
            lastGameTime = gameTime;
            lastBoundingBox = getSpawnableBlocks();
        }
        return lastBoundingBox;
    }

    private Set<BoundingBoxSpawnableBlocks> getSpawnableBlocks() {
        BoundingBoxSpawnableBlocks boundingBox = new BoundingBoxSpawnableBlocks();

        int width = MathHelper.floor(Math.pow(2, 1 + ConfigManager.spawnableBlocksRenderWidth.get()));
        int height = MathHelper.floor(Math.pow(2, ConfigManager.spawnableBlocksRenderHeight.get()));

        SpawnableBlocksHelper.findSpawnableBlocks(Player.getCoords(), width, height, boundingBox.getBlocks()::add);

        return Collections.singleton(boundingBox);
    }
}
