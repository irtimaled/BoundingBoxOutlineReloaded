package com.irtimaled.bbor.mixin.client.gui.screen;

import com.google.common.collect.Lists;
import com.irtimaled.bbor.client.gui.SettingsScreenButton;
import com.irtimaled.bbor.common.TypeHelper;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
    private MixinOptionsScreen() {
        super(null);
    }

    private final List<ClickableWidget> buttons = Lists.newArrayList();

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        ClickableWidget widget = TypeHelper.as(drawableElement, ClickableWidget.class);
        if(widget != null) {
            buttons.add(widget);
        }

        return super.addDrawableChild(drawableElement);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {
        //shuffle middle buttons up by 12 px to make space
        int top = this.height / 6 + 42;
        int bottom = this.height / 6 + 168;
        for (ClickableWidget button : buttons) {
            if (button.y >= top && button.y < bottom)
                button.y -= 12;
        }
        SettingsScreenButton button = new SettingsScreenButton(this.width / 2 - 155, top + 84, 150, "BBOR", this);
        this.addDrawableChild(button);
    }
}
