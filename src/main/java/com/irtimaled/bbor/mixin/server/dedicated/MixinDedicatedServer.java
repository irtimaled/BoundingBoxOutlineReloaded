package com.irtimaled.bbor.mixin.server.dedicated;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftDedicatedServer.class)
public class MixinDedicatedServer {
    /* Cannot inject when class is load?
    @Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;setPlayerManager(Lnet/minecraft/server/PlayerManager;)V"))
    private void init() {
        new CommonProxy().init();
    }
     */
}
