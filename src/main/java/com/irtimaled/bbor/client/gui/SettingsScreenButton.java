package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class SettingsScreenButton extends AbstractButton {
    private final SettingsScreen screen;

    public SettingsScreenButton(int x, int y, int width, String label, Screen lastScreen) {
        super(x, y, width, label);
        screen = new SettingsScreen(lastScreen, 0);
    }

    @Override
    public void onPressed() {
        MinecraftClient.getInstance().openScreen(screen);
    }
}
