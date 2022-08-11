package com.irtimaled.bbor.mixin.client.world.extras;

import com.irtimaled.bbor.client.providers.SpawnableBlocksProvider;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/light/LightingProvider;doLightUpdates(IZZ)I", shift = At.Shift.AFTER))
    private void afterLightingUpdate(CallbackInfo ci) {
        SpawnableBlocksProvider.runQueuedTasks();
    }

}
