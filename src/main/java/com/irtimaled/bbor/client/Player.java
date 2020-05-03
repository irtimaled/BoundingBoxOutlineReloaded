package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class Player {
    private static double x;
    private static double y;
    private static double z;
    private static float yaw;
    private static float pitch;
    private static int dimensionId;

    public static void setPosition(double partialTicks, ClientPlayerEntity player) {
        x = player.lastTickPosX + (player.func_226277_ct_() - player.lastTickPosX) * partialTicks;
        y = player.lastTickPosY + (player.func_226278_cu_() - player.lastTickPosY) * partialTicks;
        z = player.lastTickPosZ + (player.func_226281_cx_() - player.lastTickPosZ) * partialTicks;
        yaw =  player.rotationYaw % 360;
        pitch = player.rotationPitch;
        dimensionId = player.dimension.getId();
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

    public static int getDimensionId() {
        return dimensionId;
    }

    public static Coords getCoords() {
        return new Coords(x, y, z);
    }

    public static Point getPoint() {
        return new Point(x, y, z);
    }
    
    public static float getYaw() {
        return yaw;
    }
    
    public static float getPitch() {
        return pitch;
    }
}
