package com.irtimaled.bbor.mixin.client.gui;

import com.irtimaled.bbor.client.gui.ListScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinGuiChat {
    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/screen/Screen;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void keyPressed(CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().currentScreen instanceof ListScreen) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
