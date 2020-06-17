package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;
import net.minecraft.client.network.ClientPlayerEntity;

public class Player {
    private static double x;
    private static double y;
    private static double z;
    private static double activeY;
    private static DimensionId dimensionId;

    public static void setPosition(double partialTicks, ClientPlayerEntity player) {
        x = player.prevX + (player.getX() - player.prevX) * partialTicks;
        y = player.prevY + (player.getY() - player.prevY) * partialTicks;
        z = player.prevZ + (player.getZ() - player.prevZ) * partialTicks;
        dimensionId = DimensionId.from(player.getEntityWorld());
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

    public static DimensionId getDimensionId() {
        return dimensionId;
    }

    public static Coords getCoords() {
        return new Coords(x, y, z);
    }

    public static Point getPoint() {
        return new Point(x, y, z);
    }
}
