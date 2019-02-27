package com.irtimaled.bbor.mixin.server.management;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.PlayerLoggedIn;
import com.irtimaled.bbor.common.events.PlayerLoggedOut;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {
    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    private void playerLoggedIn(EntityPlayerMP player, CallbackInfo ci) {
        if (player.world.isRemote) {
            EventBus.publish(new PlayerLoggedIn(player));
        }
    }

    @Inject(method = "playerLoggedOut", at = @At("HEAD"))
    private void playerLoggedOut(EntityPlayerMP player, CallbackInfo ci) {
        if (player.world.isRemote) {
            EventBus.publish(new PlayerLoggedOut(player));
        }
    }
}
