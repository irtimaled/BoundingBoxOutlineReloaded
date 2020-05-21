package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.BoundingBoxCuboid;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBoxProvider implements IBoundingBoxProvider<BoundingBoxCuboid> {
    private static final Map<Integer, Map<Integer, BoundingBoxCuboid>> dimensionCache = new HashMap<>();

    private static int getHashKey(Coords minCoords, Coords maxCoords) {
        return (31 + minCoords.hashCode()) * 31 + maxCoords.hashCode();
    }

    private static Map<Integer, BoundingBoxCuboid> getCache(int dimensionId) {
        return dimensionCache.computeIfAbsent(dimensionId, i -> new ConcurrentHashMap<>());
    }

    public static void add(Coords minCoords, Coords maxCoords) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = getHashKey(minCoords, maxCoords);
        BoundingBoxCuboid cuboid = BoundingBoxCuboid.from(minCoords, maxCoords, BoundingBoxType.Custom);
        getCache(dimensionId).put(cacheKey, cuboid);
    }

    public static boolean remove(Coords minCoords, Coords maxCoords) {
        int dimensionId = Player.getDimensionId();
        int cacheKey = getHashKey(minCoords, maxCoords);
        return getCache(dimensionId).remove(cacheKey) != null;
    }

    public static void clear() {
        dimensionCache.values().forEach(Map::clear);
    }

    @Override
    public Iterable<BoundingBoxCuboid> get(int dimensionId) {
        return getCache(dimensionId).values();
    }
}
