package com.irtimaled.bbor.litemod.mixins;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements Comparable<KeyBinding> {
    @Shadow
    private static Map<String, Integer> CATEGORY_ORDER;

    @Shadow
    private String keyDescription;

    @Shadow
    private String keyCategory;

    @Overwrite()
    public int compareTo(KeyBinding p_compareTo_1_) {
        return this.keyCategory.equals(p_compareTo_1_.getKeyCategory()) ? I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.getKeyDescription())) : ((Integer) CATEGORY_ORDER.getOrDefault(this.keyCategory, 0)).compareTo(CATEGORY_ORDER.getOrDefault(p_compareTo_1_.getKeyCategory(), 0));
    }
}
