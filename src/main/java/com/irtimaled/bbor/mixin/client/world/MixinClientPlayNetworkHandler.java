package com.irtimaled.bbor.mixin.client.world;

import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "onChunkData", at = @At("RETURN"))
    private void onChunkLoad(ChunkDataS2CPacket packet, CallbackInfo ci) {
        ClientWorldUpdateTracker.onChunkLoad(packet.getX(), packet.getZ());
    }

    @Inject(method = "readLightData", at = @At("RETURN"))
    private void onLightingUpdate(int x, int z, LightData data, CallbackInfo ci) {
        ClientWorldUpdateTracker.onLightingUpdate(x, z);
    }

    @Inject(method = "method_38542", at = @At("RETURN"))
    private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket, BlockEntity blockEntity, CallbackInfo ci) {
        ClientWorldUpdateTracker.updateBlockEntity(blockEntity);
    }

}
