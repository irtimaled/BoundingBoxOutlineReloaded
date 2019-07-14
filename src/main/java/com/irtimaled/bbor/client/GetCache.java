package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;

import java.util.function.Function;

interface GetCache extends Function<Integer, BoundingBoxCache> {
}
