package com.irtimaled.bbor.mixin.server;

import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DynamicRegistryManager.class)
public interface MixinDynamicRegistryManager {

//    @Shadow <E> Optional<? extends Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key);
//
//    @Inject(method = "streamSyncedRegistries", at = @At("RETURN"), cancellable = true)
//    private void modifySyncedRegistries(CallbackInfoReturnable<Stream<DynamicRegistryManager.Entry<?>>> cir) {
//        final Optional<? extends Registry<Structure>> optional = this.getOptional(Registry.STRUCTURE_KEY);
//        optional.ifPresent(structures -> cir.setReturnValue(Stream.concat(cir.getReturnValue(),
//                Stream.of(new DynamicRegistryManager.Entry<>(Registry.STRUCTURE_KEY, structures)))));
//    }

}
