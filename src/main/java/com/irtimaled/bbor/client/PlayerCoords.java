package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerCoords {
    private static double x;
    private static double y;
    private static double z;
    private static double activeY;
    private static int dimensionId;

    public static void setPlayerPosition(double partialTicks, EntityPlayer entityPlayer) {
        x = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks;
        y = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * partialTicks;
        z = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks;
        dimensionId = entityPlayer.dimension.getId();
    }

    static void setActiveY() {
        activeY = y;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }

    public static double getZ() {
        return z;
    }

    public static double getMaxY(double configMaxY) {
        if (configMaxY == -1) {
            return activeY;
        }
        if (configMaxY == 0) {
            return y;
        }
        return configMaxY;
    }

    public static int getDimensionId() {
        return dimensionId;
    }

    public static Coords get() {
        return new Coords(x, y, z);
    }
}
