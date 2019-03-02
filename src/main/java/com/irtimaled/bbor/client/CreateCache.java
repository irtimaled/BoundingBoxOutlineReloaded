package com.irtimaled.bbor.client;

import com.irtimaled.bbor.common.BoundingBoxCache;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.Function;

public interface CreateCache extends Function<DimensionType, BoundingBoxCache> { }
