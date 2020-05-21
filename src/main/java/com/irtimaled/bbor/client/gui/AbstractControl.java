package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

import java.awt.*;

abstract class AbstractControl extends Widget implements IControl {
    private static final int PADDING = 4;
    protected final Minecraft minecraft;

    AbstractControl(int x, int y, int width, String name) {
        super(x, y, width, 20, name);
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY, 0f);
    }

    public void setX(int x) {
        super.x = x;
    }

    public void setY(int y) {
        super.y = y;
    }

    public int getControlHeight() {
        return this.height + PADDING;
    }

    public int getControlWidth() {
        return this.width + PADDING;
    }

    @Override
    protected void renderBg(Minecraft minecraft, int mouseX, int mouseY) {
        if (active) renderBackground(mouseX, mouseY);
    }

    protected void renderBackground(int mouseX, int mouseY) {
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void filter(String lowerValue) {
        String lowerString = this.getMessage().toLowerCase();
        this.visible = lowerValue.equals("") ||
                lowerString.startsWith(lowerValue) ||
                lowerString.contains(" " + lowerValue);
    }

    void drawRectangle(int left, int top, int right, int bottom, Color color) {
        fill(left, top, right, bottom, color.getRGB());
    }

    @Override
    public void clearFocus() {
        this.setFocused(false);
    }
}
