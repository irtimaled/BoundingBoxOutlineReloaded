package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BoundingBoxCache {
    private Map<AbstractBoundingBox, Set<AbstractBoundingBox>> cache = new ConcurrentHashMap<>();

    public Map<AbstractBoundingBox, Set<AbstractBoundingBox>> getBoundingBoxes() {
        return cache;
    }

    void clear() {
        cache.clear();
    }

    public boolean isCached(AbstractBoundingBox key) {
        return cache.containsKey(key);
    }

    public void addBoundingBoxes(AbstractBoundingBox key, Set<AbstractBoundingBox> boundingBoxes) {
        cache.put(key, boundingBoxes);
    }

    public void addBoundingBox(AbstractBoundingBox key) {
        if (isCached(key)) return;

        Set<AbstractBoundingBox> boundingBoxes = new HashSet<>();
        boundingBoxes.add(key);
        addBoundingBoxes(key, boundingBoxes);
    }

    void removeBoundingBox(AbstractBoundingBox key) {
        cache.remove(key);
    }
}
