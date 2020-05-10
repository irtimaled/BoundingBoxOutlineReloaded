package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.config.BoundingBoxTypeHelper;
import com.irtimaled.bbor.client.interop.SpawningSphereHelper;
import com.irtimaled.bbor.client.models.BoundingBoxSpawningSphere;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.MathHelper;
import com.irtimaled.bbor.common.models.Point;

import java.util.HashSet;
import java.util.Set;

public class SpawningSphereProvider implements IBoundingBoxProvider<BoundingBoxSpawningSphere> {
    private static BoundingBoxSpawningSphere spawningSphere;
    private static Integer dimensionId;

    public static void setSphere(double x, double y, double z) {
        Point point = new Point(snapToNearestHalf(x), y, snapToNearestHalf(z));

        if (spawningSphere != null && spawningSphere.getPoint().equals(point)) {
            return;
        }
        clear();

        dimensionId = Player.getDimensionId();
        spawningSphere = new BoundingBoxSpawningSphere(point);
    }

    private static double snapToNearestHalf(double value) {
        int floor = MathHelper.floor(value);
        int fraction = MathHelper.floor((value - floor) * 4.0);
        if (fraction % 2 == 1) fraction++;
        return floor + (fraction / 4.0);
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
            Point sphereCenter = spawningSphere.getPoint();
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

    static boolean playerInsideSphere() {
        return spawningSphereInDimension(Player.getDimensionId()) && spawningSphere.isWithinSphere(Player.getPoint());
    }

    private static boolean spawningSphereInDimension(int dimensionId) {
        return spawningSphere != null && SpawningSphereProvider.dimensionId == dimensionId;
    }

    @Override
    public boolean canProvide(int dimensionId) {
        return spawningSphereInDimension(dimensionId) && BoundingBoxTypeHelper.shouldRender(BoundingBoxType.AFKSphere);
    }

    @Override
    public Iterable<BoundingBoxSpawningSphere> get(int dimensionId) {
        Set<BoundingBoxSpawningSphere> boundingBoxes = new HashSet<>();
        boundingBoxes.add(spawningSphere);
        return boundingBoxes;
    }
}
