package com.irtimaled.bbor.litemod.mixins;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import com.mumfrey.liteloader.client.overlays.IEntityRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer {
    @Inject(method = "renderWorldPass",
            at = @At(shift = At.Shift.BEFORE,
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
                    args = "ldc=hand"))
    private void onRenderHand(int pass, float partialTicks, long timeSlice, CallbackInfo ci) {
        BoundingBoxOutlineReloaded.render(partialTicks);
    }
}