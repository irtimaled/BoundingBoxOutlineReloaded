package com.irtimaled.bbor.mixin.client.renderer;

import com.irtimaled.bbor.client.AsyncRenderer;
import com.irtimaled.bbor.client.ClientRenderer;
import com.irtimaled.bbor.client.RenderCulling;
import com.irtimaled.bbor.client.providers.SpawnableBlocksProvider;
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
        cir.getReturnValue().add(SpawnableBlocksProvider.debugString());

        if (!ClientRenderer.getActive()) {
            cir.getReturnValue().add("[BBOR] Rendering not enabled");
            return;
        }

        cir.getReturnValue().addAll(RenderCulling.debugStrings());
        cir.getReturnValue().add(AsyncRenderer.renderingDebugString());
        cir.getReturnValue().add(String.format("[BBOR] Rendering took %.2fms", AsyncRenderer.getLastDurationNanos() / 1_000_000.0));
    }

}
