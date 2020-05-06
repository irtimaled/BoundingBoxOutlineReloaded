package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.models.Coords;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {
    private static final Map<Integer, Map<Coords, BoundingBoxBeacon>> dimensionsCache = new HashMap<>();

    private static Map<Coords, BoundingBoxBeacon> getCache(int dimensionId) {
        return dimensionsCache.computeIfAbsent(dimensionId, i -> new ConcurrentHashMap<>());
    }

    public static void add(Coords coords, int level) {
        int dimensionId = Player.getDimensionId();
        BoundingBoxBeacon beacon = BoundingBoxBeacon.from(coords, level);
        getCache(dimensionId).put(coords, beacon);
    }

    public static boolean remove(Coords coords) {
        int dimensionId = Player.getDimensionId();
        return getCache(dimensionId).remove(coords) != null;
    }

    public static void clear() {
        dimensionsCache.values().forEach(Map::clear);
    }

    public Iterable<BoundingBoxBeacon> get(int dimensionId) {
        return getCache(dimensionId).values();
    }
}
