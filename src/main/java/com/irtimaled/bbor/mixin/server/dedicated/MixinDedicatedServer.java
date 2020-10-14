package com.irtimaled.bbor.mixin.server.dedicated;

import com.irtimaled.bbor.common.CommonProxy;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {
    @Inject(method = "init", at = @At(value = "NEW", target = "net/minecraft/server/dedicated/DedicatedPlayerList"))
    private void init(CallbackInfoReturnable<Boolean> cir) {
        new CommonProxy().init();
    }
}
