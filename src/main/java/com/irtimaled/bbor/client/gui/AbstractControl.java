package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

abstract class AbstractControl extends GuiButton implements IControl {
    private static final int PADDING = 4;
    protected final Minecraft minecraft;

    AbstractControl(int x, int y, int width, String name) {
        super(0, x, y, width, 20, name);
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY, 0f);
    }

    @Override
    public void setX(int x) {
        super.x = x;
    }

    @Override
    public void setY(int y) {
        super.y = y;
    }

    @Override
    public int getControlHeight() {
        return this.height + PADDING;
    }

    @Override
    public int getControlWidth() {
        return this.width + PADDING;
    }

    @Override
    protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
        if (enabled) renderBackground(mouseX, mouseY);
    }

    protected void renderBackground(int mouseX, int mouseY) {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean getVisible() {
        return this.visible;
    }

    @Override
    public void filter(String lowerValue) {
        String lowerString = this.displayString.toLowerCase();
        this.setVisible(lowerValue.equals("") ||
                lowerString.startsWith(lowerValue) ||
                lowerString.contains(" " + lowerValue));
    }

    void drawRectangle(int left, int top, int right, int bottom, Color color) {
        drawRect(left, top, right, bottom, color.getRGB());
    }
}
