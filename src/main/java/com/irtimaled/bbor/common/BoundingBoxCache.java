package com.irtimaled.bbor.common;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BoundingBoxCache {
    private final Map<AbstractBoundingBox, Set<AbstractBoundingBox>> cache = new ConcurrentHashMap<>();

    public Map<AbstractBoundingBox, Set<AbstractBoundingBox>> getBoundingBoxes() {
        return cache;
    }

    public void clear() {
        cache.clear();
    }

    public boolean isCached(AbstractBoundingBox key) {
        return cache.containsKey(key);
    }

    public void addBoundingBoxes(AbstractBoundingBox key, Set<AbstractBoundingBox> boundingBoxes) {
        cache.put(key, boundingBoxes);
    }

    public enum Type {
        LOCAL,
        REMOTE_BBOR,
        REMOTE_SERVUX
    }
}
