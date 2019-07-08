package com.irtimaled.bbor.mixin.world;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillageCollection;tick()V", shift = At.Shift.AFTER))
    private void afterVillageTick(CallbackInfo ci) {
        CommonInterop.worldTick((WorldServer) (Object) this);
    }
}
