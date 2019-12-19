package com.irtimaled.bbor.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Camera {
    private static Vec3d getPos() {
        return MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
    }

    public static double getX() {
        return getPos().x;
    }

    public static double getY() {
        return getPos().y;
    }

    public static double getZ() {
        return getPos().z;
    }
}

