package com.irtimaled.bbor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class Camera {
    private static Vec3d getPos() {
        return Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
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

