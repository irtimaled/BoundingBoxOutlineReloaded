package com.irtimaled.bbor.mixin.server.management;

import com.irtimaled.bbor.common.interop.CommonInterop;
import net.minecraft.block.Block;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager {
    @Shadow
    public ServerWorld world;

    @Inject(method = "tryHarvestBlock", at = @At("HEAD"))
    private void tryHarvestBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block block = this.world.getBlockState(pos).getBlock();
        CommonInterop.tryHarvestBlock(block, pos, world);
    }
}
