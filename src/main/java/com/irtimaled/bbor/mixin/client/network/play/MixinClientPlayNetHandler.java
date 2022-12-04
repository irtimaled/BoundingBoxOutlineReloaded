package com.irtimaled.bbor.mixin.client.network.play;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetHandler {
    @Shadow private ClientWorld world;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @Inject(method = "onUnloadChunk", at = @At("RETURN"))
    private void onChunkUnload(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        ClientInterop.unloadChunk(packet.getX(), packet.getZ());
    }

//    @Inject(method = "onSynchronizeTags", at = @At("RETURN"))
//    private void onSynchronizeTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
//        CommonInterop.loadWorldStructures(this.world);
//    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientInterop.interceptCommandUsage(command)) {
            cir.setReturnValue(true);
        }
    }
}
