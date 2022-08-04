package com.irtimaled.bbor.mixin.client.entity.player;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String message, Text preview, CallbackInfo ci) {
        if (ClientInterop.interceptChatMessage(message))
            ci.cancel();
    }
}
