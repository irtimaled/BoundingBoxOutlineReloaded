package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class SettingsScreenButton extends AbstractButton {
    private final SettingsScreen screen;

    public SettingsScreenButton(int id, int x, int y, int width, String label, GuiScreen lastScreen) {
        super(id, x, y, width, label);
        screen = new SettingsScreen(lastScreen, 0);
    }

    @Override
    public void onPressed() {
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
}
