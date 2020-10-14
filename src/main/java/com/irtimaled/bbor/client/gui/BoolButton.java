package com.irtimaled.bbor.client.gui;

import com.irtimaled.bbor.client.config.ColorHelper;
import com.irtimaled.bbor.client.config.ConfigManager;
import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class BoolButton extends AbstractButton {
    BoolButton(int width, String label, boolean enabled) {
        super(width, label, enabled);
    }

    BoolButton(int width, String label) {
        super(0, 0, width, label);
    }

    protected abstract boolean getValue();

    @Override
    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY) {
        int left = this.x + 1;
        int top = this.y + 1;
        int right = left + this.width - 2;
        int bottom = top + this.height - 2;
        if (this.getValue()) {
            drawRectangle(matrixStack, left, top, right, bottom, ColorHelper.getColor(ConfigManager.buttonOnOverlay));
        }
    }
}
