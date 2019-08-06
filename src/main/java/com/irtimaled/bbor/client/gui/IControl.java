package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.Element;

interface IControl extends IFocusableControl, Element {
    void render(int mouseX, int mouseY);

    boolean isVisible();
}
