package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Final
    @Shadow
    private static Map<String, Integer> categoryOrderMap;

    static {
        categoryOrderMap.put(KeyListener.Category, 0);
    }
}
