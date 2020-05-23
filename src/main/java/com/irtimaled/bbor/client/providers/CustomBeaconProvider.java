package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.models.BoundingBoxBeacon;
import com.irtimaled.bbor.common.BoundingBoxType;
import com.irtimaled.bbor.common.models.Coords;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomBeaconProvider implements IBoundingBoxProvider<BoundingBoxBeacon> {
    private static final Map<DimensionId, Map<Coords, BoundingBoxBeacon>> dimensionsCache = new HashMap<>();

    private static Map<Coords, BoundingBoxBeacon> getCache(DimensionId dimensionId) {
        return dimensionsCache.computeIfAbsent(dimensionId, i -> new ConcurrentHashMap<>());
    }

    public static void add(Coords coords, int level) {
        DimensionId dimensionId = Player.getDimensionId();
        BoundingBoxBeacon beacon = BoundingBoxBeacon.from(coords, level, BoundingBoxType.Custom);
        getCache(dimensionId).put(coords, beacon);
    }

    public static boolean remove(Coords coords) {
        DimensionId dimensionId = Player.getDimensionId();
        return getCache(dimensionId).remove(coords) != null;
    }

    public static void clear() {
        dimensionsCache.values().forEach(Map::clear);
    }

    @Override
    public Iterable<BoundingBoxBeacon> get(DimensionId dimensionId) {
        return getCache(dimensionId).values();
    }
}
