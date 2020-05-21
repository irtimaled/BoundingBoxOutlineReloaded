package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.models.BoundingBoxLine;
import com.irtimaled.bbor.client.models.Point;
import com.irtimaled.bbor.common.BoundingBoxType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomLineProvider implements IBoundingBoxProvider<BoundingBoxLine> {
    private static final Map<Integer, Map<Integer, BoundingBoxLine>> dimensionCache = new HashMap<>();

    private static int getHashKey(Point minPoint, Point maxPoint) {
        return (31 + minPoint.hashCode()) * 31 + maxPoint.hashCode();
    }

    private static Map<Integer, BoundingBoxLine> getCache(int dimensionId) {
        return dimensionCache.computeIfAbsent(dimensionId, i -> new ConcurrentHashMap<>());
    }

    public static void add(Point minPoint, Point maxPoint, Double width) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = getHashKey(minPoint, maxPoint);
        BoundingBoxLine line = BoundingBoxLine.from(minPoint, maxPoint, width, BoundingBoxType.Custom);
        getCache(dimensionId).put(cacheKey, line);
    }

    public static boolean remove(Point min, Point max) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = getHashKey(min, max);
        return getCache(dimensionId).remove(cacheKey) != null;
    }

    public static void clear() {
        dimensionCache.values().forEach(Map::clear);
    }

    public Iterable<BoundingBoxLine> get(int dimensionId) {
        return getCache(dimensionId).values();
    }
}
