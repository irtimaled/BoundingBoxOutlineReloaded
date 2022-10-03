package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.BlockProcessor;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Set;

public class SpawningSphereProvider implements IBoundingBoxProvider<BoundingBoxSpawningSphere>, ICachingProvider {
    public static final MinecraftClient minecraft = MinecraftClient.getInstance();
    private static Long lastGameTime = null;

    private static Set<BoundingBoxSpawningSphere> lastBoundingBox = null;
    private static volatile BoundingBoxSpawningSphere spawningSphere;
    private static DimensionId dimensionId;

    public static void setSphere(Point point) {
        if (spawningSphere != null && spawningSphere.getPoint().equals(point)) return;

        dimensionId = Player.getDimensionId();
        spawningSphere = new BoundingBoxSpawningSphere(point);
        lastBoundingBox = null;
    }

    public static boolean clearSphere() {
        if (spawningSphere != null) {
            lastBoundingBox = null;
            spawningSphere = null;
            dimensionId = null;
            return true;
        }
        return false;
    }

    public void clearCache() {
        clearSphere();
    }

    public static void calculateSpawnableSpacesCount(BlockProcessor blockProcessor) {
        if (spawningSphere != null) {
            Point sphereCenter = spawningSphere.getPoint();
            int size = BoundingBoxSpawningSphere.SPAWN_RADIUS + 2;
            SpawningSphereHelper.findSpawnableSpaces(sphereCenter, sphereCenter.getCoords(), size, size, blockProcessor);
        }
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
        return List.of(spawningSphere);
    }

}
