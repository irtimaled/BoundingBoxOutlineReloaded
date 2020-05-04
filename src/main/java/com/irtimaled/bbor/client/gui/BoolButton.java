package com.irtimaled.bbor.client.gui;

import java.awt.*;

public abstract class BoolButton extends AbstractButton {
    private static final Color OVERLAY_COLOR = new Color(0, 255, 0, 48);

    private boolean value;

    BoolButton(int width, String label, boolean enabled) {
        super(0, 0, width, label, enabled);
    }

    BoolButton(int width, String label) {
        super(0, 0, width, label);
    }

    @Override
    protected int getState() {
        return enabled ? super.getState() : 0;
    }

    protected boolean getValue() {
        return this.value;
    }

    protected void setValue(boolean value) {
        this.value = value;
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY) {
        int left = this.x + 1;
        int top = this.y + 1;
        int right = left + this.width - 2;
        int bottom = top + this.height - 2;
        if (this.getValue()) {
            drawRectangle(left, top, right, bottom, OVERLAY_COLOR);
        }
    }
}
