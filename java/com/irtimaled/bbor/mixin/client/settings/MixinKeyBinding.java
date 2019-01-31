package com.irtimaled.bbor.mixin.client.settings;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;


@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow
    private static Map<String, Integer> CATEGORY_ORDER;

    static {
        CATEGORY_ORDER.put("Bounding Box Outline Reloaded", 0);
    }
}