package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.world.gen.structure.MapGenEndCity;

public class EndChunkProcessor extends AbstractChunkProcessor {
    public EndChunkProcessor(BoundingBoxCache boundingBoxCache) {
        super(boundingBoxCache);
        supportedStructures.put(BoundingBoxType.EndCity, (chunkGenerator) -> getStructures(chunkGenerator, MapGenEndCity.class));
    }
}
