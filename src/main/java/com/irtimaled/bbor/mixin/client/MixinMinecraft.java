package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.keyboard.KeyListener;
import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(method = "runTickKeyboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V"))
    private void setKeyBindState(int keyCode, boolean pressed) {
        if (!KeyListener.setKeyBindState(keyCode, pressed)) {
            KeyBinding.setKeyBindState(keyCode, pressed);
        }
    }
}
