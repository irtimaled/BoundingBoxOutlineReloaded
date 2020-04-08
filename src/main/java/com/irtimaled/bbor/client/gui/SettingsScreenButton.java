package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class SettingsScreenButton extends AbstractButton {
    private final SettingsScreen screen;

    public SettingsScreenButton(int x, int y, int width, String label, GuiScreen lastScreen) {
        super(x, y, width, label);
        screen = new SettingsScreen(lastScreen);
    }

    @Override
    public void onPressed() {
        Minecraft.getInstance().displayGuiScreen(screen);
    }
}
