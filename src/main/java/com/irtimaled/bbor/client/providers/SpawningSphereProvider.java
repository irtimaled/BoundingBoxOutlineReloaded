package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.interop.BlockProcessor;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;

import java.util.HashSet;
import java.util.Set;

public class SpawningSphereProvider implements IBoundingBoxProvider<BoundingBoxSpawningSphere> {
    public static final MinecraftClient minecraft = MinecraftClient.getInstance();
    private static Long lastGameTime = null;

    private static Set<BoundingBoxSpawningSphere> lastBoundingBox = null;
    private static BoundingBoxSpawningSphere spawningSphere;
    private static DimensionId dimensionId;

    public static void setSphere(Point point) {
        if (spawningSphere != null && spawningSphere.getPoint().equals(point)) return;
        clear();

        dimensionId = Player.getDimensionId();
        spawningSphere = new BoundingBoxSpawningSphere(point);
        lastBoundingBox = null;
    }

    public static boolean clear() {
        if (spawningSphere != null) {
            lastBoundingBox = null;
            spawningSphere = null;
            dimensionId = null;
            return true;
        }
        return false;
    }

    public static void calculateSpawnableSpacesCount(BlockProcessor blockProcessor) {
        if (spawningSphere != null) {
            Point sphereCenter = spawningSphere.getPoint();
            int size = BoundingBoxSpawningSphere.SPAWN_RADIUS + 2;
            SpawningSphereHelper.findSpawnableSpaces(sphereCenter, sphereCenter.getCoords(), size, size, blockProcessor);
        }
    }

    static boolean playerInsideSphere() {
        return hasSpawningSphereInDimension(Player.getDimensionId()) && spawningSphere.isWithinSphere(Player.getPoint());
    }

    public static boolean hasSpawningSphereInDimension(DimensionId dimensionId) {
        return spawningSphere != null && SpawningSphereProvider.dimensionId == dimensionId;
    }

    public static void setSpawnableSpacesCount(int count) {
        if (spawningSphere != null) {
            spawningSphere.setSpawnableCount(count);
        }
    }

    @Override
    public boolean canProvide(DimensionId dimensionId) {
        return hasSpawningSphereInDimension(dimensionId) && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.AFKSphere);
    }

    @Override
    public Iterable<BoundingBoxSpawningSphere> get(DimensionId dimensionId) {
        long gameTime = minecraft.world.getTime();
        if (lastBoundingBox == null || (!((Long) gameTime).equals(lastGameTime) && gameTime % 2L == 0L)) {
            lastGameTime = gameTime;
            lastBoundingBox = getSpawningSphere();
        }
        return lastBoundingBox;
    }

    private Set<BoundingBoxSpawningSphere> getSpawningSphere() {
        spawningSphere.getBlocks().clear();
        if (ConfigManager.renderAFKSpawnableBlocks.get()) {
            int width = MathHelper.floor(Math.pow(2, 1 + ConfigManager.spawnableBlocksRenderWidth.get()));
            int height = MathHelper.floor(Math.pow(2, ConfigManager.spawnableBlocksRenderHeight.get()));

            SpawningSphereHelper.findSpawnableSpaces(spawningSphere.getPoint(), Player.getCoords(), width, height, spawningSphere.getBlocks()::add);
        }
        Set<BoundingBoxSpawningSphere> boundingBoxes = new HashSet<>();
        boundingBoxes.add(spawningSphere);
        return boundingBoxes;
    }
}
