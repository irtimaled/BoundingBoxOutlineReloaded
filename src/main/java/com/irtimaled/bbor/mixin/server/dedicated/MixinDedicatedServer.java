package com.irtimaled.bbor.mixin.server.dedicated;

import com.irtimaled.bbor.common.CommonProxy;
import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class MixinDedicatedServer {
    @Inject(method = "setupServer", at = @At(value = "NEW", target = "net/minecraft/server/dedicated/DedicatedPlayerManager"))
    private void init(CallbackInfoReturnable<Boolean> cir) {
        CommonInterop.init();
        new CommonProxy().init();
    }
}
