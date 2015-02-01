package com.irtimaled.bbor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BoundingBoxCache {

    protected ConcurrentHashMap<BoundingBox, Set<BoundingBox>> cache = new ConcurrentHashMap<BoundingBox, Set<BoundingBox>>();

    public Map<BoundingBox, Set<BoundingBox>> getBoundingBoxes() {
        return cache;
    }

    public synchronized void refresh() {
    }

    public void close() {
        cache.clear();
    }
}
