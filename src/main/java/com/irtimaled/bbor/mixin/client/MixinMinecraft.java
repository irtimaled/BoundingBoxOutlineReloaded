package com.irtimaled.bbor.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft extends ReentrantThreadExecutor<Runnable> {
    public MixinMinecraft(String string) {
        super(string);
    }

//    @Inject(method = "<init>", at = @At(value = "RETURN"))
//    private void constructor(RunArgs configuration, CallbackInfo ci) {
//        new ClientProxy().init();
//    }

//    @Inject(method = "joinWorld", at = @At("RETURN"))
//    private void onJoinWorld(ClientWorld world, CallbackInfo ci) {
//        this.send(() -> CommonInterop.loadWorldStructures(world));
//    }

}
