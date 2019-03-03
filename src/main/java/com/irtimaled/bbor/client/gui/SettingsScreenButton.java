package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SettingsScreenButton extends GuiButton {
    private final SettingsScreen screen;

    public SettingsScreenButton(int id, int x, int y, int height, int width, String label, GuiScreen lastScreen) {
        super(id, x, y, height, width, label);
        screen = new SettingsScreen(lastScreen, 0);
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        Minecraft.getInstance().displayGuiScreen(screen);
    }
}
