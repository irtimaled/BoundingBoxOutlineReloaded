package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings {
    @Shadow
    public KeyBinding[] keyBindings;

    @Shadow
    public abstract void loadOptions();

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("RETURN"))
    private void init(Minecraft minecraft, File file, CallbackInfo ci) {
        keyBindings = ArrayUtils.addAll(keyBindings, KeyListener.keyBindings());
        this.loadOptions();
    }
}
