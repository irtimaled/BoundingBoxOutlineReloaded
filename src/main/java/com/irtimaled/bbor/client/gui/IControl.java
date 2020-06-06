package com.irtimaled.bbor.client.gui;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

interface IControl extends IFocusableControl, Element {
    void render(MatrixStack matrixStack, int mouseX, int mouseY);

    boolean isVisible();
}
