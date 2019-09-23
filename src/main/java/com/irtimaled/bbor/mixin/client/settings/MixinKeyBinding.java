package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Final
    @Shadow
    private static Map<String, Integer> CATEGORY_ORDER;

    static {
        CATEGORY_ORDER.put(KeyListener.Category, 0);
    }
}
