package com.irtimaled.bbor.mixin.client.world;

import com.irtimaled.bbor.client.interop.ClientWorldUpdateTracker;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class MixinWorldChunk {

    @Shadow @Final private World world;

    @Inject(method = "setBlockEntity", at = @At("RETURN"))
    private void onSetBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
        if (this.world.isClient) {
            ClientWorldUpdateTracker.addBlockEntity(blockEntity);
        }
    }

    @Inject(method = "removeBlockEntity", at = @At("RETURN"))
    private void onRemoveBlockEntity(BlockPos pos, CallbackInfo ci) {
        if (this.world.isClient) {
            ClientWorldUpdateTracker.removeBlockEntity(pos.getX(), pos.getY(), pos.getZ());
        }
    }

}
