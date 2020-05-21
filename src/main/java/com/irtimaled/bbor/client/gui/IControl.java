package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.IGuiEventListener;

interface IControl extends IFocusableControl, IGuiEventListener {
    void render(int mouseX, int mouseY);

    boolean isVisible();
}
