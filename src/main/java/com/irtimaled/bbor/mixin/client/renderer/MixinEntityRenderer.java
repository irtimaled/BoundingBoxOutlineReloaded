package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V", args = "ldc=hand", shift = At.Shift.BEFORE))
    private void render(float partialTicks, long ignored, CallbackInfo ci) {
        ClientInterop.render(partialTicks, this.mc.player);
    }
}
