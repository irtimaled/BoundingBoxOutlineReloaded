package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        BoundingBoxOutlineReloaded.init();
    }

    @Inject(method = "processKeyBinds", at = @At("HEAD"))
    public void processKeyBinds(CallbackInfo ci) {
        BoundingBoxOutlineReloaded.keyPressed();
    }
}