package com.irtimaled.bbor.mixin.entity.player;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.PlayerChangedDimension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {
    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;changePlayerDimension(Lnet/minecraft/entity/player/EntityPlayerMP;I)V"))
    private void changeDimension(int dimensionId, CallbackInfoReturnable<Entity> cir) {
        EventBus.publish(new PlayerChangedDimension((EntityPlayerMP) (Object) this));
    }
}
