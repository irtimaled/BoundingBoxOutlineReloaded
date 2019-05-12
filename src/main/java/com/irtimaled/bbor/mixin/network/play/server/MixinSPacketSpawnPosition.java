package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.events.UpdateWorldSpawnReceived;
import com.irtimaled.bbor.common.EventBus;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SPacketSpawnPosition.class)
public abstract class MixinSPacketSpawnPosition {
    @Shadow
    private BlockPos spawnBlockPos;

    @Inject(method = "processPacket", at = @At("RETURN"))
    private void afterProcessPacket(CallbackInfo ci) {
        EventBus.publish(new UpdateWorldSpawnReceived(spawnBlockPos.getX(), spawnBlockPos.getZ()));
    }
}
