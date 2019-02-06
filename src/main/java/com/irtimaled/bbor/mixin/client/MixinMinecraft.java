package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    private ClientProxy clientProxy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(GameConfiguration configuration, CallbackInfo ci) {
        CommonInterop.init();
        clientProxy = new ClientProxy();
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        clientProxy.init();
    }
}
