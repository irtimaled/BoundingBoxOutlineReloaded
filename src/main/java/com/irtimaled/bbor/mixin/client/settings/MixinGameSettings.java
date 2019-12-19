package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class MixinGameSettings {
    @Mutable
    @Final
    @Shadow
    public KeyBinding[] keysAll;

    @Shadow
    public abstract void load();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        keysAll = ArrayUtils.addAll(keysAll, KeyListener.keyBindings());
        this.load();
    }
}
