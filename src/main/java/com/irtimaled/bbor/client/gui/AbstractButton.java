package com.irtimaled.bbor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

abstract class AbstractButton extends GuiButton implements IRenderableControl {
    AbstractButton(int id, int x, int y, int width, String name) {
        super(id, x, y, width, 20, name);
    }

    AbstractButton(int id, int x, int y, int width, String name, boolean enabled) {
        this(id, x, y, width, name);
        this.enabled = enabled;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, 0f);
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        renderBackground();
    }

    protected void renderBackground() {
    }

    @Override
    protected int getHoverState(boolean p_getHoverState_1_) {
        return getState();
    }

    protected int getState() {
        return this.enabled ? this.hovered ? 2 : 1 : 0;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            onPressed();
            return true;
        }
        return false;
    }

    protected abstract void onPressed();
}
