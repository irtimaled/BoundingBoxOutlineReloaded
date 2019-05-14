package com.irtimaled.bbor.mixin.server.management;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.TypeHelper;
import com.irtimaled.bbor.common.events.MobSpawnerBroken;
import com.irtimaled.bbor.common.models.Coords;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager {
    @Shadow
    public World world;

    @Inject(method = "tryHarvestBlock", at = @At("HEAD"))
    private void tryHarvestBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block block = this.world.getBlockState(pos).getBlock();
        TypeHelper.doIfType(block, BlockMobSpawner.class, ms ->
                EventBus.publish(new MobSpawnerBroken(this.world.dimension.getType().getId(), new Coords(pos))));
    }
}
