package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.RenderCulling;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    private void afterLeftText(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add(RenderCulling.debugString());
    }

}
