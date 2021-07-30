package com.irtimaled.bbor.mixin.client.access;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public interface IClientChunkManagerClientChunkMap {

    @Accessor
    AtomicReferenceArray<WorldChunk> getChunks();

}
