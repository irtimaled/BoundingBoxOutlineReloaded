package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class Player {
    private static double x;
    private static double y;
    private static double z;
    private static double activeY;
    private static int dimensionId;

    public static void setPosition(double partialTicks, ClientPlayerEntity player) {
        x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        dimensionId = player.dimension.getId();
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

    public static Coords getCoords() {
        return new Coords(x, y, z);
    }

    public static Point getPoint() {
        return new Point(x, y, z);
    }
}
