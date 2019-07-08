package com.irtimaled.bbor.mixin.client.gui;

import com.irtimaled.bbor.client.gui.SettingsScreenButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiOptions.class)
public class MixinGuiOptions extends GuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {
        //shuffle middle buttons up by 12 px to make space
        int top = this.height / 6 + 42;
        int bottom = this.height / 6 + 168;
        for (GuiButton button : buttons) {
            if (button.y >= top && button.y < bottom)
                button.y -= 12;
        }
        this.addButton(new SettingsScreenButton(199, this.width / 2 - 155, top + 84, 150, "BBOR", this));
    }
}
