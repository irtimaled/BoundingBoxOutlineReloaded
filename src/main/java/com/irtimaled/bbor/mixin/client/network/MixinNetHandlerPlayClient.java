package com.irtimaled.bbor.mixin.client.network;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @Inject(method="handleChunkData", at = @At("RETURN"))
    private void onChunkData(SPacketChunkData packet, CallbackInfo ci) {
        ClientInterop.receivedChunk(packet.getChunkX(), packet.getChunkZ());
    }
}
