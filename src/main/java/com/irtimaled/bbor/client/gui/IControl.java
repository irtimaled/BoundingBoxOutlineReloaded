package com.irtimaled.bbor.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;

interface IControl extends IFocusableControl, IGuiEventListener {
    void render(MatrixStack matrixStack, int mouseX, int mouseY);

    boolean isVisible();
}
