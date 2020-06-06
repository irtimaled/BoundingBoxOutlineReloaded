package com.irtimaled.bbor.mixin.resource;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ResourcePackManager.class)
public class MixinResourcePackManager<T extends ResourcePackProfile> {
    private static final String BBOR = "bbor";
    @Shadow
    @Final
    private ResourcePackProfile.Factory<T> profileFactory;
    private T resourcePackProfile;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterConstructor(CallbackInfo ci) {
        resourcePackProfile = ResourcePackProfile.of(BBOR,
                true,
                () -> new DefaultResourcePack(BBOR),
                this.profileFactory,
                ResourcePackProfile.InsertionPosition.BOTTOM,
                ResourcePackSource.PACK_SOURCE_BUILTIN);
    }

    @Redirect(method = "providePackProfiles", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"))
    private ImmutableMap<String, T> beforeReturn(Map<String, T> map) {
        map.put(BBOR, resourcePackProfile);
        return ImmutableMap.copyOf(map);
    }
}
