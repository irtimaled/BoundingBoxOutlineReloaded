package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;
import com.irtimaled.bbor.common.models.DimensionId;

public interface IBoundingBoxProvider<T extends AbstractBoundingBox> {
    Iterable<T> get(DimensionId dimensionId);

    default boolean canProvide(DimensionId dimensionId) {
        return true;
    }

    default void cleanup() {
    }
}
