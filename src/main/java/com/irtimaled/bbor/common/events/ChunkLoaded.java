package com.irtimaled.bbor.common.events;

import net.minecraft.world.chunk.WorldChunk;

public class ChunkLoaded {
    private final WorldChunk chunk;
    private final int dimensionId;

    public ChunkLoaded(WorldChunk chunk) {
        this.chunk = chunk;
        this.dimensionId = chunk.getWorld().getDimension().getType().getRawId();
    }

    public WorldChunk getChunk() {
        return chunk;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
