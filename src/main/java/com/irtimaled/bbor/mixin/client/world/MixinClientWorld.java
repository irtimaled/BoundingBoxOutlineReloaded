package com.irtimaled.bbor.mixin.client.world;

import com.irtimaled.bbor.client.interop.ClientInterop;
import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "disconnect", at = @At("RETURN"))
    private void sendQuittingDisconnectingPacket(CallbackInfo ci) {
        ClientInterop.disconnectedFromRemoteServer();
    }

    @Inject(method = "updateListeners", at = @At("RETURN"))
    private void onUpdateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        ClientWorldUpdateTracker.onBlockChange(pos.getX(), pos.getY(), pos.getZ(), oldState, newState);
    }

}
