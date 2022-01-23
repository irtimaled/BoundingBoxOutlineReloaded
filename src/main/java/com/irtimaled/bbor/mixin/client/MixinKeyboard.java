package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Keyboard.class)
public class MixinKeyboard {

    /**
     * inject right after the {@code window == this.client.getWindow().getHandle()} check that's at the top of onKey
     * if that's missing or there's something before it in future versions may need to set to "HEAD" and re-add it here
     * also may need to change the injection point to be right after the if statment if it isn't a {@code debugCrashStartTime} field
     */
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), method = "onKey", cancellable = true)
    public void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (KeyListener.onKeyEvent(window, key, scancode, action, modifiers)) {
            ci.cancel();
        }
    }
}
