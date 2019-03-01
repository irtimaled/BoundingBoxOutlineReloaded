package com.irtimaled.bbor.mixin.village;

import com.irtimaled.bbor.common.EventBus;
import com.irtimaled.bbor.common.events.VillageUpdated;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Village.class)
public abstract class MixinVillage {
    @Shadow
    protected abstract void updateVillagerCount();

    @Shadow
    private int villagerCount;

    @Shadow private World world;

    @Inject(method = "updateVillageRadiusAndCenter", at = @At("HEAD"))
    private void updateVillageRadiusAndCenter(CallbackInfo ci) {
        Village village = (Village) (Object) this;
        publishVillageUpdated(village);
    }

    private void publishVillageUpdated(Village village) {
        EventBus.publish(new VillageUpdated(world.dimension.getType(), village));
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Village;updateVillagerCount()V"))
    private void tick(Village village) {
        int population = this.villagerCount;
        this.updateVillagerCount();
        if (this.villagerCount != population)
            publishVillageUpdated(village);
    }
}
