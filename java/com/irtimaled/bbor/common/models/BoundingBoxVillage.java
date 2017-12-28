package com.irtimaled.bbor.common.models;

import com.irtimaled.bbor.common.VillageColorCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class BoundingBoxVillage extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;
    private final boolean spawnsIronGolems;
    private Set<BlockPos> doors;
    private Double centerOffsetX;
    private Double centerOffsetZ;
    private int villageHash;

    private BoundingBoxVillage(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, Set<BlockPos> doors, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
        this.villageHash = computeHash(center, radius, spawnsIronGolems, doors);
        calculateCenterOffsets(doors);
    }

    public static BoundingBoxVillage from(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, Set<BlockPos> doors) {
        BlockPos minBlockPos = new BlockPos(center.getX() - radius,
                center.getY() - 4,
                center.getZ() - radius);
        BlockPos maxBlockPos = new BlockPos(center.getX() + radius,
                center.getY() + 4,
                center.getZ() + radius);
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, doors, minBlockPos, maxBlockPos);
    }

    public static BoundingBoxVillage from(BlockPos center, Integer radius, int villageId, int population, Set<BlockPos> doors) {
        Boolean spawnsIronGolems = shouldSpawnIronGolems(population, doors.size());
        Color color = VillageColorCache.getColor(villageId);
        return from(center, radius, color, spawnsIronGolems, doors);
    }

    private static boolean shouldSpawnIronGolems(int population, int doorCount) {
        return population >= 10 && doorCount >= 21;
    }

    public static BoundingBoxVillage from(Village village) {
        BlockPos center = village.getCenter();
        int radius = village.getVillageRadius();
        Set<BlockPos> doors = getDoorsFromVillage(village);
        return from(center, radius, village.hashCode(), village.getNumVillagers(), doors);
    }

    private static Set<BlockPos> getDoorsFromVillage(Village village) {
        Set<BlockPos> doors = new HashSet<>();
        for (Object doorInfo : village.getVillageDoorInfoList()) {
            VillageDoorInfo villageDoorInfo = (VillageDoorInfo) doorInfo;
            doors.add(villageDoorInfo.getDoorBlockPos());
        }
        return doors;
    }

    private void calculateCenterOffsets(Set<BlockPos> doors) {
        boolean processedFirstDoor = false;
        int minX = 0;
        int maxX = 0;
        int minZ = 0;
        int maxZ = 0;
        for (BlockPos door : doors) {
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

    public BlockPos getCenter() {
        return center;
    }

    public Double getCenterOffsetX() {
        return centerOffsetX;
    }

    public Double getCenterOffsetZ() {
        return centerOffsetZ;
    }

    private static int computeHash(BlockPos center, Integer radius, boolean spawnsIronGolems, Set<BlockPos> doors) {
        int result = (center.hashCode() * 31) + radius;
        for (BlockPos door : doors) {
            result = (31 * result) + door.hashCode();
        }
        if (spawnsIronGolems) {
            result = 31 * result;
        }
        return result;
    }

    public boolean matches(Village village) {
        return this.villageHash == computeHash(village.getCenter(),
                village.getVillageRadius(),
                shouldSpawnIronGolems(village.getNumVillagers(), village.getNumVillageDoors()),
                getDoorsFromVillage(village));
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31) + villageHash;
    }

    public boolean getSpawnsIronGolems() {
        return spawnsIronGolems;
    }

    public Set<BlockPos> getDoors() {
        return doors;
    }
}
