package com.irtimaled.bbor.client.gui;

public abstract class BoolButton extends AbstractButton {
    private boolean value;

    BoolButton(int width, String label, boolean enabled) {
        super(0, 0, width, label, enabled);
    }

    BoolButton(int width, String label) {
        super(0, 0, width, label);
    }

    @Override
    protected int getState() {
        return enabled ? this.getValue() ? 2 : 1 : 0;
    }

    protected boolean getValue() {
        return this.value;
    }

    protected void setValue(boolean value) {
        this.value = value;
    }
}
