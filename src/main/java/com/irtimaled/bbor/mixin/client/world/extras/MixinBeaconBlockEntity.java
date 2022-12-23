package com.irtimaled.bbor.mixin.client.world.extras;

import com.irtimaled.bbor.client.providers.BeaconProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/BeaconBlockEntity;level:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private static void onLevelChange(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (world.isClient) {
            BeaconProvider.updateOrCreateBeacon(blockEntity);
        }
    }

}
