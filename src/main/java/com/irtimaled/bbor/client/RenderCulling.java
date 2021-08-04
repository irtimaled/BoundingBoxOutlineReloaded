package com.irtimaled.bbor.client;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;

import java.util.concurrent.atomic.AtomicInteger;

public class RenderCulling {

    private static volatile Frustum frustum = null;
    private static final Object mutex = new Object();
    private static final AtomicInteger culledCount = new AtomicInteger();
    private static final AtomicInteger totalCount = new AtomicInteger();
    private static final AtomicInteger culledCountLast = new AtomicInteger();
    private static final AtomicInteger totalCountLast = new AtomicInteger();

    public static void setFrustum(Frustum frustum) {
        RenderCulling.frustum = frustum;
    }

    public static void flushStats() {
        synchronized (mutex) {
            culledCountLast.set(culledCount.get());
            totalCountLast.set(totalCount.get());
            culledCount.set(0);
            totalCount.set(0);
        }
    }

    public static String debugString() {
        final int culledCountLast;
        final int totalCountLast;
        synchronized (mutex) {
            culledCountLast = RenderCulling.culledCountLast.get();
            totalCountLast = RenderCulling.totalCountLast.get();
        }
        if (totalCountLast != 0) {
            return String.format("[BBOR] Rendering culling: %d / %d (%.1f%%)", culledCountLast, totalCountLast, (culledCountLast / (float) totalCountLast) * 100.0);
        } else {
            return "[BBOR] Rendering not enabled";
        }
    }

    private static boolean cullFrustum(Box box) {
        final Frustum frustum = RenderCulling.frustum;
        if (frustum != null) {
            return frustum.isVisible(box);
        } else {
            return true;
        }
    }

    public static boolean isVisibleCulling(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return isVisibleCulling(new Box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static boolean isVisibleCulling(Box box) {
        final boolean cullResult = cullFrustum(box);
        totalCount.incrementAndGet();
        if (!cullResult) culledCount.incrementAndGet();
        return cullResult;
    }

}
