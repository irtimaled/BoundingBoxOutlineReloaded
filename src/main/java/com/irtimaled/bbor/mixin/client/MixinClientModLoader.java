package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import net.minecraftforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientModLoader.class)
public class MixinClientModLoader {

    @Inject(method = "completeModLoading", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;start()V", shift = At.Shift.AFTER), remap = false)
    private static void afterEventBusStart(CallbackInfoReturnable<Boolean> ci) {
        new ClientProxy().init();
    }

}
