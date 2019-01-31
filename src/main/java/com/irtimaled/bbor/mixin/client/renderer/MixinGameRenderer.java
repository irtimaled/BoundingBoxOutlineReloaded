package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=hand", shift = At.Shift.BEFORE))
    private void render(float partialTicks, long ignored, CallbackInfo ci) {
        BoundingBoxOutlineReloaded.render(partialTicks);
    }
}
