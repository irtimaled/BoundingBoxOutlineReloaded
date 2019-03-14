package com.irtimaled.bbor.mixin.world;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.ServerWorldTick;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @Inject(method = "tick", at=@At(value = "INVOKE", target = "Lnet/minecraft/village/VillageCollection;tick()V", shift = At.Shift.AFTER))
    private void afterVillageTick(CallbackInfo ci) {
        EventBus.publish(new ServerWorldTick((WorldServer)(Object)this));
    }

}
