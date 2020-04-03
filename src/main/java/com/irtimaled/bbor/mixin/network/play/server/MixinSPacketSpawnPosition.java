package com.irtimaled.bbor.mixin.network.play.server;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSpawnPositionS2CPacket.class)
public abstract class MixinSPacketSpawnPosition {
    @Shadow
    private BlockPos pos;

    @Inject(method = "apply", at = @At("RETURN"))
    private void afterProcessPacket(CallbackInfo ci) {
        ClientInterop.updateWorldSpawnReceived(pos);
    }
}
