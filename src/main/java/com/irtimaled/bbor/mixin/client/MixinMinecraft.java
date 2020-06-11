package com.irtimaled.bbor.mixin.client;

import com.irtimaled.bbor.common.ReflectionHelper;
import com.irtimaled.bbor.client.ClientProxy;
import com.irtimaled.bbor.client.interop.ModPackFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public class MixinMinecraft {
    @Shadow
    @Final
    private ResourcePackManager<ClientResourcePackProfile> resourcePackManager;
    private ClientProxy clientProxy;

    private static final Function<ResourcePackManager, Set<ResourcePackProvider>> providersFetcher =
        ReflectionHelper.getPrivateFieldGetter(ResourcePackManager.class, Set.class, ResourcePackProvider.class);
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(RunArgs configuration, CallbackInfo ci) {
        clientProxy = new ClientProxy();
        clientProxy.init();
        Set<ResourcePackProvider> providers = providersFetcher.apply(this.resourcePackManager);
        providers.add(new ModPackFinder());

    }

}
