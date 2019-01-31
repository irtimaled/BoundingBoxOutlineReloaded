package com.irtimaled.bbor.mixin.server;

import com.irtimaled.bbor.client.BoundingBoxOutlineReloaded;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

    @Shadow @Final private Map<DimensionType, WorldServer> worlds;

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
    private void initialWorldChunkLoad(CallbackInfo ci)
    {
        for(World world : worlds.values()) {
            BoundingBoxOutlineReloaded.worldLoaded(world);
        }
    }
}
