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
        super.render(mouseX, mouseY, 0f);
    }

    @Override
    protected void renderBg(Minecraft p_renderBg_1_, int p_renderBg_2_, int p_renderBg_3_) {
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
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        onPressed();
    }

    protected abstract void onPressed();
}
