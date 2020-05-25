package com.irtimaled.bbor.mixin.client.world;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "sendQuittingDisconnectingPacket", at = @At("RETURN"))
    private void sendQuittingDisconnectingPacket(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }
}
