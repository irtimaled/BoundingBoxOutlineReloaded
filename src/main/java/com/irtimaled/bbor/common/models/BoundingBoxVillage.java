package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.VillageColorCache;
import com.irtimaled.bbor.common.VillageHelper;

import java.awt.*;
import java.util.Set;

public class BoundingBoxVillage extends BoundingBox {
    private final Coords center;
    private final Integer radius;
    private final boolean spawnsIronGolems;
    private final Color color;
    private Set<Coords> doors;
    private Double centerOffsetX;
    private Double centerOffsetZ;
    private int villageHash;

    private BoundingBoxVillage(Coords center, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors, Coords minCoords, Coords maxCoords) {
        super(minCoords, maxCoords, BoundingBoxType.Village);
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
        this.villageHash = VillageHelper.computeHash(center, radius, spawnsIronGolems, doors);
        calculateCenterOffsets(doors);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, Color color, boolean spawnsIronGolems, Set<Coords> doors) {
        Coords minCoords = new Coords(center.getX() - radius,
                center.getY() - 4,
                center.getZ() - radius);
        Coords maxCoords = new Coords(center.getX() + radius,
                center.getY() + 4,
                center.getZ() + radius);
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, doors, minCoords, maxCoords);
    }

    public static BoundingBoxVillage from(Coords center, Integer radius, int villageId, int population, Set<Coords> doors) {
        boolean spawnsIronGolems = VillageHelper.shouldSpawnIronGolems(population, doors.size());
        Color color = VillageColorCache.getColor(villageId);
        return from(center, radius, color, spawnsIronGolems, doors);
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
        centerOffsetX = Math.abs(maxX - minX) % 2 == 0 ? 0.5 : (minX < 0 ? 0 : 1);
        centerOffsetZ = Math.abs(maxZ - minZ) % 2 == 0 ? 0.5 : (minZ < 0 ? 0 : 1);
    }

    @Override
    public String toString() {
        return "(" + center.toString() + "; " + radius.toString() + ")";
    }

    public Integer getRadius() {
        return radius;
    }

    public Coords getCenter() {
        return center;
    }

    public Color getColor() {
        return color;
    }

    public Double getCenterOffsetX() {
        return centerOffsetX;
    }

    public Double getCenterOffsetZ() {
        return centerOffsetZ;
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31) + villageHash;
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
