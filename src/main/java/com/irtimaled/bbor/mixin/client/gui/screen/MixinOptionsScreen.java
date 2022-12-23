package com.irtimaled.bbor.mixin.client.gui.screen;

import com.irtimaled.bbor.client.config.ConfigManager;
import com.irtimaled.bbor.client.gui.SettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen extends Screen {
    @Shadow protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    private MixinOptionsScreen() {
        super(null);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addSettingsButton(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder) {
        if (ConfigManager.showSettingsButton.get()) {
            adder.add(this.createButton(Text.of("BBOR"), () -> new SettingsScreen(this)));
        }
    }
}
