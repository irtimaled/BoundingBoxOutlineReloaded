package com.irtimaled.bbor.common.chunkProcessors;

import com.irtimaled.bbor.common.BoundingBoxCache;
import com.irtimaled.bbor.common.BoundingBoxType;

public class NetherChunkProcessor extends AbstractChunkProcessor {
    public NetherChunkProcessor(BoundingBoxCache boundingBoxCache) {
        super(boundingBoxCache);
        supportedStructures.add(BoundingBoxType.NetherFortress);
    }
}
