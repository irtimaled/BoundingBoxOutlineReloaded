package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class NetherChunkProcessor extends AbstractChunkProcessor {
    public NetherChunkProcessor(BoundingBoxCache boundingBoxCache) {
        super(boundingBoxCache);
        supportedStructures.put(BoundingBoxType.NetherFortress, (chunkGenerator) -> getStructures(chunkGenerator, MapGenNetherBridge.class));
    }
}
