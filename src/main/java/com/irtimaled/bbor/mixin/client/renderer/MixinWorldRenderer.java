package com.irtimaled.bbor.mixin.client.renderer;

import com.google.common.base.Preconditions;
import com.irtimaled.bbor.client.Player;
import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Shadow @Final private MinecraftClient client;

    @Shadow private Frustum frustum;

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(MatrixStack matrixStack, float partialTicks, long ignored_2, boolean ignored_3, Camera ignored_4, GameRenderer ignored_5, LightmapTextureManager ignored_6, Matrix4f ignored_7, CallbackInfo ci) {
        Preconditions.checkNotNull(this.client.player);
        RenderCulling.setFrustum(frustum);
        RenderCulling.flushStats();
        Player.setPosition(partialTicks, this.client.player);
        ClientInterop.render(matrixStack, this.client.player);
        ClientInterop.renderDeferred();
    }

}
