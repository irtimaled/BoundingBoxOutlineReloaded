package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageHelper;

import java.awt.*;
import java.util.Set;

public class BoundingBoxVillage extends BoundingBoxSphere {
    private final boolean spawnsIronGolems;
    private final Color color;
    private final Coords center;
    private final Set<Coords> doors;
    private final int villageHash;

    private BoundingBoxVillage(Point point, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors) {
        super(point, radius, BoundingBoxType.VillageSpheres);
        this.center = point.getCoords();
        this.color = color;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
        this.villageHash = VillageHelper.computeHash(this.center, radius, spawnsIronGolems, doors);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors) {
        Point point = calculateCenterPoint(center, doors);
        return new BoundingBoxVillage(point, radius, color, spawnsIronGolems, doors);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, int villageId, int population, Set<Coords> doors) {
        boolean spawnsIronGolems = VillageHelper.shouldSpawnIronGolems(population, doors.size());
        Color color = VillageColorCache.getColor(villageId);
        return from(center, radius, color, spawnsIronGolems, doors);
    }

    private static Point calculateCenterPoint(Coords center, Set<Coords> doors) {
        boolean processedFirstDoor = false;
        int minX = 0;
        int maxX = 0;
        int minZ = 0;
        int maxZ = 0;
        for (Coords door : doors) {
            if (!processedFirstDoor ||
                    (minX > door.getX()))
                minX = door.getX();
            if (!processedFirstDoor ||
                    maxX < door.getX())
                maxX = door.getX();
            if (!processedFirstDoor ||
                    minZ > door.getZ())
                minZ = door.getZ();
            if (!processedFirstDoor ||
                    maxZ < door.getZ())
                maxZ = door.getZ();

            processedFirstDoor = true;
        }

        double x = Math.abs(maxX - minX) % 2 == 0 ? 0.5 : (minX < 0 ? 0 : 1);
        double z = Math.abs(maxZ - minZ) % 2 == 0 ? 0.5 : (minZ < 0 ? 0 : 1);
        return new Point(center).offset(x, 0.0D, z);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        return TypeHelper.combineHashCodes(super.hashCode(), villageHash);
    }

    public boolean getSpawnsIronGolems() {
        return spawnsIronGolems;
    }

    public Set<Coords> getDoors() {
        return doors;
    }

    public int getVillageHash() {
        return villageHash;
    }

    public Coords getCenter() {
        return center;
    }
}
