package com.irtimaled.bbor.mixin.client.multiplayer;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @Inject(method = "sendQuittingDisconnectingPacket", at = @At("RETURN"))
    private void sendQuittingDisconnectingPacket(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }
}
