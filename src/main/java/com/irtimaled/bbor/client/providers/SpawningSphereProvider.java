package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Coords;

public class SpawningSphereProvider implements IBoundingBoxProvider<BoundingBoxSpawningSphere> {
    private static BoundingBoxSpawningSphere spawningSphere;
    private static Integer dimensionId;

    public static void setSphere(double x, double y, double z) {
        Coords coords = new Coords(x, y, z);
        double xOffset = snapToNearestHalf(x -coords.getX());
        double yOffset = y- coords.getY();
        double zOffset = snapToNearestHalf(z-coords.getZ());

        if(spawningSphere != null && spawningSphere.isCenter(coords, xOffset, yOffset, zOffset)) {
            return;
        }
        clear();

        dimensionId = Player.getDimensionId();
        spawningSphere = new BoundingBoxSpawningSphere(coords, xOffset, yOffset, zOffset);
    }

    private static double snapToNearestHalf(double value) {
        int floor = MathHelper.floor(value * 4.0);
        if(floor % 2 == 1) floor += 1;
        return floor / 4.0;
    }

    public static boolean clear() {
        if(spawningSphere != null) {
            spawningSphere = null;
            dimensionId = null;
            return true;
        }
        return false;
    }

    public static int recalculateSpawnableSpacesCount() {
        if (spawningSphere != null) {
            Point sphereCenter = new Point(spawningSphere.getCenter())
                    .offset(spawningSphere.getCenterOffsetX(),
                            spawningSphere.getCenterOffsetY(),
                            spawningSphere.getCenterOffsetZ());
            int spawnableSpacesCount = getSpawnableSpacesCount(sphereCenter);
            spawningSphere.setSpawnableCount(spawnableSpacesCount);
            return spawnableSpacesCount;
        }
        return -1;
    }

    private static int getSpawnableSpacesCount(Point center) {
        int size = BoundingBoxSpawningSphere.SPAWN_RADIUS + 2;
        return SpawningSphereHelper.findSpawnableSpaces(center, center.getCoords(), size, size, (x, y, z) -> true);
    }

    private static final Iterable<BoundingBoxSpawningSphere> iterable = Iterators.singleton(() -> spawningSphere);

    public Iterable<BoundingBoxSpawningSphere> get(int dimensionId) {
        if(spawningSphere == null || SpawningSphereProvider.dimensionId != dimensionId) {
            return Iterators.empty();
        }
        return iterable;
    }
}
