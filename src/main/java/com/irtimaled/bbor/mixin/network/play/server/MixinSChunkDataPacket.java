package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkDataS2CPacket.class)
public class MixinSChunkDataPacket {
    @Shadow
    private int chunkX;

    @Shadow
    private int chunkZ;

    @Inject(method = "apply", at = @At("RETURN"))
    private void processPacket(ClientPlayPacketListener netHandlerPlayClient, CallbackInfo ci) {
        ClientInterop.receivedChunk(this.chunkX, this.chunkZ);
    }
}
