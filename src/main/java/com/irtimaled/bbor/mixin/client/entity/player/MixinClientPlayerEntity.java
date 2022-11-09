package com.irtimaled.bbor.mixin.client.entity.player;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommand(String command, CallbackInfo ci) {
        if (ClientInterop.interceptCommandUsage(command))
            ci.cancel();
    }
}
