package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameSettings.class)
public class MixinGameSettings {
    @Shadow
    public KeyBinding[] keyBindings;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        keyBindings = getKeysAll();
    }

    private KeyBinding[] getKeysAll() {
        return ArrayUtils.addAll(keyBindings, KeyListener.keyBindings());
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("RETURN"))
    private void init(Minecraft minecraft, File file, CallbackInfo ci) {
        keyBindings = getKeysAll();
    }
}
