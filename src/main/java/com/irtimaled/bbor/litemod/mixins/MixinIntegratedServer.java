package com.irtimaled.bbor.litemod.mixins;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer {
    public MixinIntegratedServer() {
        super(null, null, null, null, null, null, null);
    }

    @Inject(method = "loadAllWorlds",
            at = @At("RETURN"),
            remap = false)
    private void onLoadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String generatorOptions, CallbackInfo ci) {
        for (World world : this.worlds) {
            BoundingBoxOutlineReloaded.worldLoaded(world);
        }
    }
}