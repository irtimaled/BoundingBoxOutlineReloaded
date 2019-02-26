package com.irtimaled.bbor.common.events;

import net.minecraft.world.chunk.Chunk;

public class ChunkLoaded {
    private final Chunk chunk;

    public ChunkLoaded(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
