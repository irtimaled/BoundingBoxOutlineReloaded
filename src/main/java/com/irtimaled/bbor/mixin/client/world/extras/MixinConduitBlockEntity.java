package com.irtimaled.bbor.mixin.client.world.extras;

import com.irtimaled.bbor.client.providers.ConduitProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConduitBlockEntity.class)
public class MixinConduitBlockEntity {

    @Inject(method = "clientTick", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/ConduitBlockEntity;active:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private static void onLevelChange(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, CallbackInfo ci) {
        if (world.isClient) {
            ConduitProvider.updateOrCreateConduit(blockEntity);
        }
    }

}
