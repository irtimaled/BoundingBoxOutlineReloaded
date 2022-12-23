package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft extends ReentrantThreadExecutor<Runnable> {
    public MixinMinecraft(String string) {
        super(string);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void constructor(RunArgs configuration, CallbackInfo ci) {
        new ClientProxy().init();
    }

    @Inject(method = "joinWorld", at = @At("RETURN"))
    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
//        CommonInterop.loadWorldStructures(world);
    }

}
