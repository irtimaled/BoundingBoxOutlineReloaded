package com.irtimaled.bbor.mixin.server;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    @Final
    private Map<DimensionType, ServerWorld> worlds;

    @Inject(method = "func_213186_a", at = @At("HEAD"))
    private void initialWorldChunkLoad(CallbackInfo ci) {
        CommonInterop.loadWorlds(worlds.values());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        CommonInterop.tick();
    }
}
