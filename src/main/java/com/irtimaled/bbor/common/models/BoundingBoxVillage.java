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
    private final Set<Coords> doors;
    private final int villageHash;

    private BoundingBoxVillage(Coords center, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors) {
        super(center, radius, BoundingBoxType.VillageSpheres);
        this.color = color;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
        this.villageHash = VillageHelper.computeHash(center, radius, spawnsIronGolems, doors);
        calculateCenterOffsets(doors);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors) {
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, doors);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, int villageId, int population, Set<Coords> doors) {
        boolean spawnsIronGolems = VillageHelper.shouldSpawnIronGolems(population, doors.size());
        Color color = VillageColorCache.getColor(villageId);
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, doors);
    }

    private void calculateCenterOffsets(Set<Coords> doors) {
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
        setCenterOffsets(Math.abs(maxX - minX) % 2 == 0 ? 0.5 : (minX < 0 ? 0 : 1), 0.0d, Math.abs(maxZ - minZ) % 2 == 0 ? 0.5 : (minZ < 0 ? 0 : 1));
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
}
