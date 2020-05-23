package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.models.DimensionId;

import java.util.function.Function;

public interface GetCache extends Function<DimensionId, BoundingBoxCache> {
}
