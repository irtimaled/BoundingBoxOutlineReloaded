package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.PlayerCoords;
import com.irtimaled.bbor.client.events.Render;
import com.irtimaled.bbor.common.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=hand", shift = At.Shift.BEFORE))
    private void render(float partialTicks, long ignored, CallbackInfo ci) {
        EntityPlayerSP player = this.mc.player;
        PlayerCoords.setPlayerPosition(partialTicks, player);
        EventBus.publish(new Render(DimensionType.getById(player.dimension)));
    }
}
