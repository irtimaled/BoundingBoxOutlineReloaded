package com.irtimaled.bbor.mixin.client.access;

import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientChunkManager.class)
public interface IClientChunkManager {

    @Accessor
    ClientChunkManager.ClientChunkMap getChunks();

}
