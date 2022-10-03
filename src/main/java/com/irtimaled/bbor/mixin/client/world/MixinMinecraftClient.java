package com.irtimaled.bbor.mixin.client.world;

import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void onSetWorld(ClientWorld world, CallbackInfo ci) {
        ClientWorldUpdateTracker.reset();
    }

}
