package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;

interface IControl extends IFocusableControl, Element {
    void render(DrawContext ctx, int mouseX, int mouseY);

    default void update() {
    }

    boolean isVisible();
}
