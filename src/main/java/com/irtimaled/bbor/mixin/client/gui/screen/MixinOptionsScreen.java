package com.irtimaled.bbor.mixin.client.gui.screen;

import com.irtimaled.bbor.client.gui.SettingsScreenButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
    private MixinOptionsScreen() {
        super(null);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {
        //shuffle middle buttons up by 12 px to make space
        int top = this.height / 6 + 42;
        int bottom = this.height / 6 + 168;
        children().stream()
                .filter(element -> element instanceof ClickableWidget)
                .map(element -> (ClickableWidget) element)
                .forEach(button -> {
                    if (button.y >= top && button.y < bottom)
                        button.y -= 12;
                });
        SettingsScreenButton button = new SettingsScreenButton(this.width / 2 - 155, top + 84, 150, "BBOR", this);
        addDrawableChild(button);
    }
}
