package com.irtimaled.bbor.mixin.client.settings;

import com.irtimaled.bbor.client.keyboard.KeyListener;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow @Final private static Set<String> KEY_CATEGORIES;

    static {
        KEY_CATEGORIES.add(KeyListener.Category);
    }
}
