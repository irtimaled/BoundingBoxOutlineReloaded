package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.GuiButton;

class Button extends GuiButton implements IRenderableControl {
    Button(int id, int x, int y, int width, String name) {
        super(id, x, y, width, 20, name);
    }

    Button(int id, int x, int y, int width, String name, boolean enabled) {
        this(id, x,y,width,name);
        this.enabled = enabled;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY, 0f);
    }
}
