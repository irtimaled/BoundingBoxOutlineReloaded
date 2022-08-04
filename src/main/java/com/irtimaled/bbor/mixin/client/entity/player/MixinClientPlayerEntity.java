package com.irtimaled.bbor.mixin.client.entity.player;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "sendCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientInterop.interceptCommandUsage(command))
            cir.setReturnValue(true);
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void interceptSendCommandWithPreview(String command, Text preview, CallbackInfo ci) {
        if (ClientInterop.interceptCommandUsage(command))
            ci.cancel();
    }


}
