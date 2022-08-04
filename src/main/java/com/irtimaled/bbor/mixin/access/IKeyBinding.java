package com.irtimaled.bbor.mixin.access;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface IKeyBinding {

    @Accessor
    static Map<String, Integer> getCATEGORY_ORDER_MAP() {
        throw new AbstractMethodError();
    }

}
