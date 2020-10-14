package com.irtimaled.bbor.mixin.server;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = "loadInitialChunks", at = @At("HEAD"))
    private void initialWorldChunkLoad(CallbackInfo ci) {
        CommonInterop.loadWorlds(worlds.values());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        CommonInterop.tick();
    }
}
