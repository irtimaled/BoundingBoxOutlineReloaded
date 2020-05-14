package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxSphere;
import com.irtimaled.bbor.common.models.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomSphereProvider implements IBoundingBoxProvider<BoundingBoxSphere> {
    private static final Map<Integer, Map<Integer, BoundingBoxSphere>> dimensionCache = new HashMap<>();

    private static Map<Integer, BoundingBoxSphere> getCache(int dimensionId) {
        return dimensionCache.computeIfAbsent(dimensionId, i -> new ConcurrentHashMap<>());
    }

    public static void add(Point center, double radius) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = center.hashCode();
        BoundingBoxSphere sphere = new BoundingBoxSphere(center, radius, BoundingBoxType.Custom);
        getCache(dimensionId).put(cacheKey, sphere);
    }

    public static boolean remove(Point center) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = center.hashCode();
        return getCache(dimensionId).remove(cacheKey) != null;
    }

    public static void clear() {
        dimensionCache.values().forEach(Map::clear);
    }

    public Iterable<BoundingBoxSphere> get(int dimensionId) {
        return getCache(dimensionId).values();
    }
}
