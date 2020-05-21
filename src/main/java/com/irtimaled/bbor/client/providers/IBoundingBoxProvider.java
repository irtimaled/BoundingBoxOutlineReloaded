package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;

public interface IBoundingBoxProvider<T extends AbstractBoundingBox> {
    Iterable<T> get(int dimensionId);

    default boolean canProvide(int dimensionId) {
        return true;
    }
}
