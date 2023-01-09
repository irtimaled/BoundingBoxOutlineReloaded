package com.irtimaled.bbor.mixin.server;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {

//    @Inject(method = "method_17227", at = @At("TAIL"))
//    private void onChunkLoad(ChunkHolder chunkHolder, Chunk protoChunk, CallbackInfoReturnable<Chunk> cir) {
//        CommonInterop.chunkLoaded((WorldChunk) cir.getReturnValue());
//    }

}
