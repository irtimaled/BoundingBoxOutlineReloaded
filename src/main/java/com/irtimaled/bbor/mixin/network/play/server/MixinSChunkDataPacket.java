package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.play.server.SChunkDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SChunkDataPacket.class)
public class MixinSChunkDataPacket {
    @Shadow
    private int chunkX;

    @Shadow
    private int chunkZ;

    @Inject(method = "processPacket", at = @At("RETURN"))
    private void processPacket(IClientPlayNetHandler netHandlerPlayClient, CallbackInfo ci) {
        ClientInterop.receivedChunk(this.chunkX, this.chunkZ);
    }
}
