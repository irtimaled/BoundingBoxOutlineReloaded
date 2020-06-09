package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;DDD)V", shift = At.Shift.BEFORE))
    private void renderFirst(MatrixStack ignored_1, float partialTicks, long ignored_2, boolean ignored_3, ActiveRenderInfo ignored_4, GameRenderer ignored_5, LightTexture ignored_6, Matrix4f ignored_7, CallbackInfo ci) {
        ClientInterop.render(partialTicks, this.mc.player);
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderDebug(Lnet/minecraft/client/renderer/ActiveRenderInfo;)V", shift = At.Shift.BEFORE))
    private void render(MatrixStack ignored_1, float partialTicks, long ignored_2, boolean ignored_3, ActiveRenderInfo ignored_4, GameRenderer ignored_5, LightTexture ignored_6, Matrix4f ignored_7, CallbackInfo ci) {
        ClientInterop.renderDeferred();
    }
}
