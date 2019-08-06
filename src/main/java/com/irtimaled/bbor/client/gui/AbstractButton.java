package com.irtimaled.bbor.client.gui;

import net.minecraft.client.MinecraftClient;

abstract class AbstractButton extends AbstractControl {
    AbstractButton(int x, int y, int width, String name) {
        super(x, y, width, name);
    }

    AbstractButton(int width, String name, boolean enabled) {
        this(0, 0, width, name);
        this.active = enabled;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        onPressed();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.active && this.visible) {
            if (key != 257 && key != 32 && key != 335) {
                return false;
            } else {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onPressed();
                return true;
            }
        } else {
            return false;
        }
    }

    protected abstract void onPressed();
}

