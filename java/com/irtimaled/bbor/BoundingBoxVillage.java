package com.irtimaled.bbor;

import net.minecraft.util.BlockPos;

import java.awt.*;

public class BoundingBoxVillage extends BoundingBox {
    private final BlockPos center;
    private final Integer radius;
    private final boolean spawnsIronGolems;

    private static int colorIndex = 0;

    protected BoundingBoxVillage(BlockPos center, Integer radius, Color color, boolean spawnsIronGolems, BlockPos minBlockPos, BlockPos maxBlockPos) {
        super(minBlockPos, maxBlockPos, color);
        this.center = center;
        this.radius = radius;
        this.spawnsIronGolems = spawnsIronGolems;
    }


    public static BoundingBox from(BlockPos center, Integer radius, int numVillagers, int numVillageDoors) {
        Color color = getVillageColor(colorIndex % 6);
        ++colorIndex;
        Boolean spawnsIronGolems = numVillagers >= 10 && numVillageDoors >= 21;
        return from(center, radius, spawnsIronGolems, color);
    }

    public static BoundingBox from(BlockPos center, Integer radius, boolean spawnsIronGolems, Color color) {
        BlockPos minBlockPos = new BlockPos(center.getX() - radius,
                center.getY() - 4,
                center.getZ() - radius);
        BlockPos maxBlockPos = new BlockPos(center.getX() + radius,
                center.getY() + 4,
                center.getZ() + radius);
        return new BoundingBoxVillage(center, radius, color, spawnsIronGolems, minBlockPos, maxBlockPos);
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

    public boolean getSpawnsIronGolems() {
        return spawnsIronGolems;
    }

    private static Color getVillageColor(int c) {
        switch (c) {
            case 0:
                return Color.RED;
            case 1:
                return Color.MAGENTA;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.CYAN;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.YELLOW;
        }
        return Color.WHITE;
    }
}