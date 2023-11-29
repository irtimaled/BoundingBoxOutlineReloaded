package com.irtimaled.bbor.mixin.server.network;

import com.irtimaled.bbor.common.BBORCustomPayload;
import com.irtimaled.bbor.common.interop.CommonInterop;
import com.irtimaled.bbor.common.messages.PayloadReader;
import com.irtimaled.bbor.common.messages.SubscribeToServer;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class MixinServerCommonNetworkHandler {

    @Inject(method = "onCustomPayload", at = @At(value = "HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        if (((Object) this) instanceof ServerPlayNetworkHandler handler) {
            if (packet.payload() instanceof BBORCustomPayload payload) {
                PayloadReader reader = new PayloadReader(payload);
                switch (payload.id().toString()) {
                    case SubscribeToServer.NAME -> {
                        CommonInterop.playerSubscribed(handler.player);
                    }
                }
                ci.cancel();
            }
        }
    }
}
