package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.interop.ClientInterop;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

public class SettingsScreenButton extends AbstractButton {
    private final SettingsScreen screen;

    public SettingsScreenButton(int x, int y, int width, String label, Screen lastScreen) {
        super(x, y, width, label);
        screen = new SettingsScreen(lastScreen);
    }

    @Override
    public void onPressed() {
        ClientInterop.displayScreen(screen);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
