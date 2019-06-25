package com.irtimaled.bbor.common.events;

import net.minecraft.world.chunk.Chunk;

public class ChunkLoaded {
    private final Chunk chunk;
    private final int dimensionId;

    public ChunkLoaded(Chunk chunk) {
        this.chunk = chunk;
        this.dimensionId = chunk.getWorld().provider.getDimensionType().getId();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
