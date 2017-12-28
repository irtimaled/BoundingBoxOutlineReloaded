package com.irtimaled.bbor;

import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Set;

public class BoundingBoxVillage extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;
    private final boolean spawnsIronGolems;
    private Set<BlockPos> doors;
    private Double centerOffsetX;
    private Double centerOffsetZ;

    protected BoundingBoxVillage(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, Set<BlockPos> doors, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
        calculateCenterOffsets(doors);
    }

    public static BoundingBoxVillage from(BlockPos center, Integer radius, int population, Set<BlockPos> doors) {
        return from(center, radius, null, population, doors);
    }

    public static BoundingBoxVillage from(BlockPos center, Integer radius, Color color, int population, Set<BlockPos> doors) {
        Boolean spawnsIronGolems = population >= 10 && doors.size() >= 21;
        return from(center, radius, color, spawnsIronGolems, doors);
    }

    public static BoundingBoxVillage from(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, Set<BlockPos> doors) {
        BlockPos minBlockPos = new BlockPos(center.getX() - radius,
                center.getY() - 4,
                center.getZ() - radius);
        BlockPos maxBlockPos = new BlockPos(center.getX() + radius,
                center.getY() + 4,
                center.getZ() + radius);
        if (color == null)
            color = getNextColor();
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, doors, minBlockPos, maxBlockPos);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        for (BlockPos door : doors) {
            result = prime * result + door.hashCode();
        }
        return result;
    }

    public boolean getSpawnsIronGolems() {
        return spawnsIronGolems;
    }

    private static int colorIndex = -1;

    public static Color getNextColor() {
        ++colorIndex;
        switch (colorIndex % 6) {
            case 0:
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.MAGENTA;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.CYAN;
        }
        return Color.WHITE;
    }

    public Set<BlockPos> getDoors() {
        return doors;
    }
}
