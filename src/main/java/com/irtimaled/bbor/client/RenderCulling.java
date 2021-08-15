package com.irtimaled.bbor.client;

import com.irtimaled.bbor.client.config.ConfigManager;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderCulling {

    private static volatile Frustum frustum = null;
    private static final Object mutex = new Object();
    private static final AtomicInteger culledCount = new AtomicInteger();
    private static final AtomicInteger totalCount = new AtomicInteger();
    private static final AtomicInteger culledCountLast = new AtomicInteger();
    private static final AtomicInteger totalCountLast = new AtomicInteger();
    private static final AtomicInteger preCulledCountLast = new AtomicInteger();
    private static final AtomicInteger preTotalCountLast = new AtomicInteger();

    public static void setFrustum(Frustum frustum) {
        RenderCulling.frustum = frustum;
    }

    public static void flushRendering() {
        synchronized (mutex) {
            culledCountLast.set(culledCount.get());
            totalCountLast.set(totalCount.get());
            culledCount.set(0);
            totalCount.set(0);
        }
    }

    public static void flushPreRendering() {
        synchronized (mutex) {
            preCulledCountLast.set(culledCount.get());
            preTotalCountLast.set(totalCount.get());
            culledCount.set(0);
            totalCount.set(0);
        }
    }

    public static List<String> debugStrings() {
        if (!ClientRenderer.getActive()) return List.of("[BBOR] Rendering not enabled");
        final ArrayList<String> list = new ArrayList<>(2);
        if (ConfigManager.fastRender.get() >= 2) {
            final int preCulledCountLast;
            final int preTotalCountLast;
            synchronized (mutex) {
                preCulledCountLast = RenderCulling.preCulledCountLast.get();
                preTotalCountLast = RenderCulling.preTotalCountLast.get();
            }
            list.add(String.format("[BBOR] Pre-culling: %d / %d (%.1f%%)", preCulledCountLast, preTotalCountLast, (preCulledCountLast / (float) preTotalCountLast) * 100.0));
        }
        if (ConfigManager.fastRender.get() >= 1) {
            final int culledCountLast;
            final int totalCountLast;
            synchronized (mutex) {
                culledCountLast = RenderCulling.culledCountLast.get();
                totalCountLast = RenderCulling.totalCountLast.get();
            }
            list.add(String.format("[BBOR] Rendering culling: %d / %d (%.1f%%)", culledCountLast, totalCountLast, (culledCountLast / (float) totalCountLast) * 100.0));
        }
        return list;
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

    public static void incrementCulling() {
        totalCount.incrementAndGet();
    }

}
