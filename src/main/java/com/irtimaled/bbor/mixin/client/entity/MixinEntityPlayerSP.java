package com.irtimaled.bbor.mixin.client.entity;

import com.irtimaled.bbor.client.ClientProxy;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String message, CallbackInfo ci) {
        if(message.startsWith("/bbor:seed")) {
            if(message.length()<11) return;

            String argument = message.substring(11);
            Long seed = parseNumericSeed(argument);
            if(seed == null) {
                seed = (long) argument.hashCode();
            }
            ClientProxy.getInstance().setSeed(seed);
            ci.cancel();
        }
    }

    private Long parseNumericSeed(String argument) {
        try {
            return Long.parseLong(argument);
        } catch (final NumberFormatException ex) {
           return null;
        }
    }
}
