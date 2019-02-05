package com.irtimaled.bbor.litemod.mixins;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class MixinChunk {
    @Inject(method = "onLoad",
            at = @At("RETURN"))
    private void onLoaded(CallbackInfo ci) {
        BoundingBoxOutlineReloaded.chunkLoaded((Chunk)(Object)this);
    }
}