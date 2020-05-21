package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.interop.ModPackFinder;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    @Final
    private ResourcePackList<ClientResourcePackInfo> resourcePackRepository;
    private ClientProxy clientProxy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(GameConfiguration configuration, CallbackInfo ci) {
        clientProxy = new ClientProxy();
        this.resourcePackRepository.addPackFinder(new ModPackFinder());
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        clientProxy.init();
    }
}
