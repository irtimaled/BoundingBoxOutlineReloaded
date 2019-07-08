package com.irtimaled.bbor.mixin.server;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    public WorldServer[] worlds;

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
    private void initialWorldChunkLoad(CallbackInfo ci) {
        CommonInterop.loadWorlds(worlds);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        CommonInterop.tick();
    }
}
