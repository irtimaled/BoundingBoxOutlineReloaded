package com.irtimaled.bbor.mixin.world.chunk;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.ChunkLoaded;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class MixinChunk {
    @Inject(method = "onLoad", at = @At("RETURN"))
    private void onLoad(CallbackInfo ci) {
        EventBus.publish(new ChunkLoaded((Chunk) (Object) this));
    }
}
