package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.interop.ModPackFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraft {
    @Shadow
    @Final
    private ResourcePackManager<ClientResourcePackProfile> resourcePackManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(RunArgs configuration, CallbackInfo ci) {
        new ClientProxy().init();
    }

    @Inject(method = "startTimerHackThread", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.resourcePackManager.registerProvider(new ModPackFinder());
    }
}
