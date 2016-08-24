package com.irtimaled.bbor;

import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Set;

public class BoundingBoxVillage extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;
    private final boolean spawnsIronGolems;
    private Set<BlockPos> doors;

    protected BoundingBoxVillage(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, Set<BlockPos> doors, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
        this.spawnsIronGolems = spawnsIronGolems;
        this.doors = doors;
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

    @Override
    public int hashCode()
    { final int prime = 31;
        int result = super.hashCode();
        for(BlockPos door : doors) {
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