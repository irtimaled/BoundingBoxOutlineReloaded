package com.ostlerdev.bbreloaded;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BoundingBoxCache {

    private Map<BoundingBox, Set<BoundingBox>> cache = new ConcurrentHashMap<BoundingBox, Set<BoundingBox>>();

    public Map<BoundingBox, Set<BoundingBox>> getBoundingBoxes() {
        return cache;
    }

    public synchronized void refresh() {
    }

    public void close() {
        cache.clear();
    }

    public boolean isCached(BoundingBox key) {
        return cache.containsKey(key);
    }

    public void addBoundingBoxes(BoundingBox key, Set<BoundingBox> boundingBoxes) {
        cache.put(key, boundingBoxes);
    }

    public void addBoundingBox(BoundingBox key) {
        Set<BoundingBox> boundingBoxes = new HashSet<BoundingBox>();
        boundingBoxes.add(key);
        addBoundingBoxes(key, boundingBoxes);
    }

    public void removeBoundingBox(BoundingBox key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        }
    }
}