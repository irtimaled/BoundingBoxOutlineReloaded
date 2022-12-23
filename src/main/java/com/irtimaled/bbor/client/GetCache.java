package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.models.DimensionId;

public interface GetCache {

    BoundingBoxCache apply(BoundingBoxCache.Type type, DimensionId dimensionId);

}
