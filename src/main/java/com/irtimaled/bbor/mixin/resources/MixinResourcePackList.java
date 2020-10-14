package com.irtimaled.bbor.mixin.resources;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.VanillaPack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ResourcePackList.class)
public class MixinResourcePackList {
    private static final String BBOR = "bbor";
    @Shadow
    @Final
    private ResourcePackInfo.IFactory packInfoFactory;
    private ResourcePackInfo resourcePackInfo;

    @Inject(method = "<init>(Lnet/minecraft/resources/ResourcePackInfo$IFactory;[Lnet/minecraft/resources/IPackFinder;)V",
            at = @At("RETURN"))
    private void afterConstructor(CallbackInfo ci) {
        resourcePackInfo = ResourcePackInfo.createResourcePack(BBOR,
                true,
                () -> new VanillaPack(BBOR),
                this.packInfoFactory,
                ResourcePackInfo.Priority.BOTTOM,
                IPackNameDecorator.BUILTIN);
    }

    @Redirect(method = "func_232624_g_", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"))
    private ImmutableMap<String, ResourcePackInfo> beforeReturn(Map<String, ResourcePackInfo> map) {
        map.put(BBOR, resourcePackInfo);
        return ImmutableMap.copyOf(map);
    }
}
