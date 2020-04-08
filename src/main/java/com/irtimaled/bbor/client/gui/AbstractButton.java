package com.irtimaled.bbor.client.gui;

abstract class AbstractButton extends AbstractControl {
    AbstractButton(int x, int y, int width, String name) {
        super(x, y, width, name);
    }

    AbstractButton(int x, int y, int width, String name, boolean enabled) {
        this(x, y, width, name);
        this.enabled = enabled;
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

